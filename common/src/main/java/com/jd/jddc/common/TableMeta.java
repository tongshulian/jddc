package com.jd.jddc.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/14.
 * Version:1.0
 */
public class TableMeta {
    private String          schema;
    private String          table;
    private List<FieldMeta> fields = new ArrayList<FieldMeta>();
    private String          ddl;                                          // 表结构的DDL语句

    public TableMeta(){

    }

    public TableMeta(String schema, String table, List<FieldMeta> fields){
        this.schema = schema;
        this.table = table;
        this.fields = fields;
    }

    public String getFullName() {
        return schema + "." + table;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<FieldMeta> getFields() {
        return fields;
    }

    public void setFields(List<FieldMeta> fileds) {
        this.fields = fileds;
    }

    public FieldMeta getFieldMetaByName(String name) {
        for (FieldMeta meta : fields) {
            if (meta.getColumnName().equalsIgnoreCase(name)) {
                return meta;
            }
        }

        throw new RuntimeException("unknow column : " + name);
    }

    public List<FieldMeta> getPrimaryFields() {
        List<FieldMeta> primarys = new ArrayList<FieldMeta>();
        for (FieldMeta meta : fields) {
            if (meta.isKey()) {
                primarys.add(meta);
            }
        }

        return primarys;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    public void addFieldMeta(FieldMeta fieldMeta) {
        this.fields.add(fieldMeta);
    }

    @Override
    public String toString() {
        StringBuilder data = new StringBuilder();
        data.append("TableMeta [schema=" + schema + ", table=" + table + ", fileds=");
        for (FieldMeta field : fields) {
            data.append("\n\t").append(field.toString());
        }
        data.append("\n]");
        return data.toString();
    }
}
