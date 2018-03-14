package com.jd.jddc.common;

import com.jd.jddc.common.enums.Operation;

import java.util.List;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/11.
 * Version:1.0
 */
public class DataRow {
    private String dbName;
    private String tableName;
    private Operation operator;
    private List<DataColumn> columns;
    private List<DataColumn> changColumns;


    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Operation getOperator() {
        return operator;
    }

    public void setOperator(Operation operator) {
        this.operator = operator;
    }

    public List<DataColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DataColumn> columns) {
        this.columns = columns;
    }

    public List<DataColumn> getChangColumns() {
        return changColumns;
    }

    public void setChangColumns(List<DataColumn> changColumns) {
        this.changColumns = changColumns;
    }

    @Override
    public String toString() {
        return "DataRow{" +
                "dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", operator=" + operator +
                ", columns=" + columns +
                ", changColumns=" + changColumns +
                '}';
    }
}
