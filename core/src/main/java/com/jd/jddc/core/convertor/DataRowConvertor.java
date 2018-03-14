package com.jd.jddc.core.convertor;

import com.jd.jddc.common.DataColumn;
import com.jd.jddc.common.DataRow;
import com.jd.jddc.common.FieldMeta;
import com.jd.jddc.common.TableMeta;
import com.jd.jddc.common.cache.TableMetaContext;
import com.jd.jddc.common.enums.Operation;
import com.taobao.tddl.dbsync.binlog.LogEvent;
import com.taobao.tddl.dbsync.binlog.event.RowsLogBuffer;
import com.taobao.tddl.dbsync.binlog.event.RowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.TableMapLogEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/14.
 * Version:1.0
 */
public class DataRowConvertor {
    private static final Logger log = LoggerFactory.getLogger(DataRowConvertor.class);

    public static DataRow covert(RowsLogEvent logEvent, String charset){
        DataRow dataRow = new DataRow();

        TableMapLogEvent table = logEvent.getTable();
        checkTable(table, logEvent.getTableId());

        dataRow.setDbName(table.getDbName());
        dataRow.setTableName(table.getTableName());
        int type = logEvent.getHeader().getType();
        if (LogEvent.WRITE_ROWS_EVENT_V1 == type || LogEvent.WRITE_ROWS_EVENT == type) {
            dataRow.setOperator(Operation.INSERT);
        } else if (LogEvent.UPDATE_ROWS_EVENT_V1 == type || LogEvent.UPDATE_ROWS_EVENT == type) {
            dataRow.setOperator(Operation.UPDATE);
        }
        fillRows(logEvent, dataRow, charset);

        return dataRow;
    }

    private static void checkTable(TableMapLogEvent table, long tableId){
        if (table == null) {
            // tableId对应的记录不存在
            throw new RuntimeException("not found tableId:" + tableId);
        }
    }

    private static void fillRows(RowsLogEvent event, DataRow dataRow, String charset){
        RowsLogBuffer buffer = event.getRowsBuf(charset);
        BitSet columns = event.getColumns();
        BitSet changeColumns = event.getChangeColumns();

        while (buffer.nextOneRow(columns)) {
            if (Operation.INSERT == dataRow.getOperator()) {
                // insert
                dataRow.setColumns(parseColumns(event, buffer, columns, dataRow));
            } else {
                // update
                dataRow.setColumns(parseColumns(event, buffer, columns, dataRow));
                if (!buffer.nextOneRow(changeColumns)) {
                    break;
                }
                dataRow.setChangColumns(parseColumns(event, buffer, changeColumns, dataRow));
            }
        }
    }

    private static List<DataColumn> parseColumns(RowsLogEvent event, RowsLogBuffer buffer,
                                                 BitSet cols, DataRow dataRow){
        int columnCnt = event.getTable().getColumnCnt();
        TableMapLogEvent table = event.getTable();
        TableMapLogEvent.ColumnInfo[] columnInfo = table.getColumnInfo();

        // 在做一次判断
        TableMeta tableMeta = TableMetaContext.get(table.getTableName());
        if (tableMeta != null && columnInfo.length > tableMeta.getFields().size()) {
            throw new RuntimeException("解析日志异常，日志中的表结构不合法");
        }
        List<DataColumn> dataColumns = new ArrayList<DataColumn>();
        for (int i = 0; i < columnCnt; i++) {
            TableMapLogEvent.ColumnInfo info = columnInfo[i];
            // mysql 5.6开始支持nolob/mininal类型,并不一定记录所有的列,需要进行判断
            if (!cols.get(i)) {
                continue;
            }

            try {
                DataColumn column = makeDataColumn(i, info, buffer, tableMeta);
                // 设置是否update的标记位
                List<DataColumn> oldColumns = dataRow.getColumns();
                column.setUpdated(isUpdate(oldColumns, column));
                if(oldColumns != null && oldColumns.size() > i && !column.isUpdated()) {
                    continue;
                }
                dataColumns.add(column);
            }catch (Exception e) {
                log.error(String.format("LogEvent解析异常，[db=%s,table=%s,column=%s]",
                        table.getDbName(), table.getTableName(), info.toString()), e);
            }
        }

        return dataColumns;
    }

    private static boolean isUpdate(List<DataColumn> oldColumns, DataColumn column){
        if(oldColumns == null || oldColumns.size() <= column.getIndex()){
            return false;
        }
        DataColumn oldColumn = oldColumns.get(column.getIndex());
        if(oldColumn.getName() != null && oldColumn.getName().equals(column.getName())
                && oldColumn.getValue() != null && !oldColumn.getValue().equals(column.getValue())){
            return true;
        }

        return false;
    }

    private static DataColumn makeDataColumn(int index, TableMapLogEvent.ColumnInfo info,
                                             RowsLogBuffer buffer, TableMeta tableMeta) throws Exception{


        DataColumn column  = new DataColumn();
        FieldMeta fieldMeta = null;
        if (tableMeta != null) {
            // 处理file meta
            fieldMeta = tableMeta.getFields().get(index);
            column.setName(fieldMeta.getColumnName());
            column.setKey(fieldMeta.isKey());
            // 增加mysql type类型
            column.setMysqlType(fieldMeta.getColumnType());
        }
        column.setIndex(index);
        column.setIsNull(false);
        boolean isBinary = false;
        if (fieldMeta != null) {
            if (StringUtils.containsIgnoreCase(fieldMeta.getColumnType(), "VARBINARY")) {
                isBinary = true;
            } else if (StringUtils.containsIgnoreCase(fieldMeta.getColumnType(), "BINARY")) {
                isBinary = true;
            }
        }
        buffer.nextValue(info.type, info.meta, isBinary);

        int javaType = buffer.getJavaType();
        if (buffer.isNull()) {
            column.setIsNull(true);
            column.setSqlType(javaType);
        } else {
            final Serializable value = buffer.getValue();
            try{
                fillColumnValue(value, javaType, column);
            }catch (UnsupportedEncodingException e){
                throw new Exception("cols=" + column.toString());
            }
        }

        return column;
    }

    private static void fillColumnValue(Serializable value, int javaType, DataColumn col)
            throws UnsupportedEncodingException{
        if(javaType == Types.DECIMAL){
            col.setValue(((BigDecimal) value).toPlainString());
            col.setSqlType(javaType);
        }else if(javaType == Types.BINARY || javaType == Types.VARBINARY
                || javaType == Types.LONGVARBINARY){
            // mysql binlog中blob/text都处理为blob类型，需要反查table
            // meta，按编码解析text
            // byte数组，直接使用iso-8859-1保留对应编码，浪费内存
            col.setValue(new String((byte[]) value, "ISO-8859-1"));
            col.setSqlType(Types.BLOB);
        }else if(javaType == Types.TIMESTAMP || javaType == Types.TIME
                || javaType == Types.DATE || javaType == Types.CHAR
                || javaType == Types.VARCHAR){
            col.setValue(value.toString());
            col.setSqlType(javaType);
        }else {
            col.setValue(String.valueOf(value));
            col.setSqlType(javaType);
        }
    }
}
