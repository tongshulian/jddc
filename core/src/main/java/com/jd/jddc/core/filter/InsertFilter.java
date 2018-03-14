package com.jd.jddc.core.filter;

import com.taobao.tddl.dbsync.binlog.LogEvent;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/13.
 * Version:1.0
 */
public class InsertFilter implements LogFilter {

    @Override
    public boolean invoke(LogEvent event) {
        int eventType = event.getHeader().getType();
        if(eventType == LogEvent.WRITE_ROWS_EVENT_V1
                || eventType == LogEvent.WRITE_ROWS_EVENT){

            return true;
        }

        return false;
    }
}
