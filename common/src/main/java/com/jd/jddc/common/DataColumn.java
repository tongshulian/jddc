package com.jd.jddc.common;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/11.
 * Version:1.0
 */
public class DataColumn {
    private int index;
    private boolean isNull;
    private String value;
    private int sqlType;
    // 设置是否update的标记位
    private boolean updated;
    private String name;
    private boolean isKey;
    private String MysqlType;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setIsNull(boolean isNull) {
        this.isNull = isNull;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isKey() {
        return isKey;
    }

    public void setKey(boolean key) {
        isKey = key;
    }

    public String getMysqlType() {
        return MysqlType;
    }

    public void setMysqlType(String mysqlType) {
        MysqlType = mysqlType;
    }

    @Override
    public String toString() {
        return "DataColumn{" +
                "index=" + index +
                ", isNull=" + isNull +
                ", value='" + value + '\'' +
                ", sqlType=" + sqlType +
                ", updated=" + updated +
                ", name='" + name + '\'' +
                ", isKey=" + isKey +
                ", MysqlType='" + MysqlType + '\'' +
                '}';
    }
}
