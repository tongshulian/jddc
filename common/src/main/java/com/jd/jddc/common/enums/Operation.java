package com.jd.jddc.common.enums;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/11.
 * Version:1.0
 */
public enum Operation {
    INSERT(1, "insert"), UPDATE(2, "update");

    private int code;
    private String desc;

    Operation(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int code(){
        return code;
    }

    public String desc(){
        return desc;
    }
}
