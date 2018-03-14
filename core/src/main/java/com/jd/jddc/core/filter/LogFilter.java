package com.jd.jddc.core.filter;


import com.taobao.tddl.dbsync.binlog.LogEvent;

/**
 * Dept: 日志事件处理类
 * User: tongshulian
 * Date:2018/2/24.
 * Version:1.0
 */
public interface LogFilter {

    boolean invoke(LogEvent event);
}
