package com.jd.jddc.example;

import com.jd.jddc.common.DataRow;
import com.jd.jddc.core.handler.LogHandler;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/19.
 * Version:1.0
 */
public class DemoLogHandler implements LogHandler {

    @Override
    public boolean handle(DataRow row) {
        if(row != null){
            System.out.println("----demo----," + row);
        }
        return true;
    }
}
