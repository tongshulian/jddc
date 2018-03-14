package com.jd.jddc.core.filter;

import com.taobao.tddl.dbsync.binlog.LogEvent;
import com.taobao.tddl.dbsync.binlog.event.RowsLogEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/13.
 * Version:1.0
 */
public class OperationFilterChain {
    List<LogFilter> chain = new ArrayList<LogFilter>();

    public RowsLogEvent invoke(LogEvent event){
        for(LogFilter filter : chain){
            if(filter.invoke(event)){
                return (RowsLogEvent) event;
            }
        }

        return null;
    }

    public boolean addFilter(LogFilter filter){
        return chain.add(filter);
    }

    public List<LogFilter> getChain() {
        return chain;
    }

    public void setChain(List<LogFilter> chain) {
        this.chain = chain;
    }
}
