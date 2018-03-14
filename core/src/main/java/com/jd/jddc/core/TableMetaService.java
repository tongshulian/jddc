package com.jd.jddc.core;

import com.alibaba.otter.canal.parse.driver.mysql.packets.server.FieldPacket;
import com.alibaba.otter.canal.parse.driver.mysql.packets.server.OKPacket;
import com.alibaba.otter.canal.parse.driver.mysql.packets.server.ResultSetPacket;
import com.jd.jddc.common.FieldMeta;
import com.jd.jddc.common.TableMeta;
import com.jd.jddc.common.cache.TableMetaContext;
import com.jd.jddc.connection.Connector;
import com.jd.jddc.connection.SqlCmdExecutor;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/11.
 * Version:1.0
 */
public class TableMetaService {
    public static final String              COLUMN_NAME    = "COLUMN_NAME";
    public static final String              COLUMN_TYPE    = "COLUMN_TYPE";
    public static final String              IS_NULLABLE    = "IS_NULLABLE";
    public static final String              COLUMN_KEY     = "COLUMN_KEY";
    public static final String              COLUMN_DEFAULT = "COLUMN_DEFAULT";
    public static final String              EXTRA          = "EXTRA";
    private Connector connector;

    public TableMetaService(Connector connector){
        this.connector = connector;
    }

    private List<FieldMeta> parserTableMeta(ResultSetPacket packet) {
        Map<String, Integer> nameMaps = new HashMap<String, Integer>(6, 1f);

        int index = 0;
        for (FieldPacket fieldPacket : packet.getFieldDescriptors()) {
            nameMaps.put(fieldPacket.getOriginalName(), index++);
        }

        int size = packet.getFieldDescriptors().size();
        int count = packet.getFieldValues().size() / packet.getFieldDescriptors().size();
        List<FieldMeta> result = new ArrayList<FieldMeta>();
        for (int i = 0; i < count; i++) {
            FieldMeta meta = new FieldMeta();
            // 做一个优化，使用String.intern()，共享String对象，减少内存使用
            meta.setColumnName(packet.getFieldValues().get(nameMaps.get(COLUMN_NAME) + i * size).intern());
            meta.setColumnType(packet.getFieldValues().get(nameMaps.get(COLUMN_TYPE) + i * size));
            meta.setNullable(StringUtils.equalsIgnoreCase(packet.getFieldValues()
                            .get(nameMaps.get(IS_NULLABLE) + i* size), "YES"));
            meta.setKey("PRI".equalsIgnoreCase(packet.getFieldValues()
                    .get(nameMaps.get(COLUMN_KEY) + i * size)));
            // 特殊处理引号
            meta.setDefaultValue(unescapeQuotaName(packet.getFieldValues()
                    .get(nameMaps.get(COLUMN_DEFAULT) + i * size)));
            meta.setExtra(packet.getFieldValues().get(nameMaps.get(EXTRA) + i * size));

            result.add(meta);
        }

        return result;
    }

    private String unescapeQuotaName(String name) {
        if (name != null && name.length() > 2) {
            char c0 = name.charAt(0);
            char x0 = name.charAt(name.length() - 1);
            if (c0 == '\'' && x0 == '\'') {
                return name.substring(1, name.length() - 1);
            }
        }

        return name;
    }

    public ResultSetPacket query(String cmd) throws IOException{
        return SqlCmdExecutor.query(connector.getChannel(), cmd);
    }

    public OKPacket update(String cmd) throws IOException{
        return SqlCmdExecutor.update(connector.getChannel(), cmd);
    }

    public void fillContext() throws IOException{
        String schema = connector.getDefaultSchema();
        update("use " + schema);
        ResultSetPacket packet = query("show tables");
        for(String tableName : packet.getFieldValues()){
            packet = query("desc " + tableName);
            TableMetaContext.put(tableName, new TableMeta(schema, tableName, parserTableMeta(packet)));
        }

    }
}
