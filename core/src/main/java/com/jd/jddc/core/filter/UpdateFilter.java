package com.jd.jddc.core.filter;

import com.taobao.tddl.dbsync.binlog.LogEvent;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/11.
 * Version:1.0
 */
public class UpdateFilter implements LogFilter {
    @Override
    public boolean invoke(LogEvent event) {
        int eventType = event.getHeader().getType();
        if(eventType == LogEvent.UPDATE_ROWS_EVENT_V1
                || eventType == LogEvent.UPDATE_ROWS_EVENT){

            return true;
        }

        return false;
    }
}
