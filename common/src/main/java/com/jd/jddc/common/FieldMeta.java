package com.jd.jddc.common;

import org.apache.commons.lang.StringUtils;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/11.
 * Version:1.0
 */
public class FieldMeta {
    public FieldMeta(){

    }

    public FieldMeta(String columnName, String columnType, boolean nullable, boolean key, String defaultValue){
        this.columnName = columnName;
        this.columnType = columnType;
        this.nullable = nullable;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    private String  columnName;
    private String  columnType;
    private boolean nullable;
    private boolean key;
    private String  defaultValue;
    private String  extra;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isUnsigned() {
        return StringUtils.containsIgnoreCase(columnType, "unsigned");
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String toString() {
        return "FieldMeta [columnName=" + columnName + ", columnType=" + columnType + ", defaultValue="
                + defaultValue + ", nullable=" + nullable + ", key=" + key + "]";
    }
}
