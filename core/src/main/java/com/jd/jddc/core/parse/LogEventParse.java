package com.jd.jddc.core.parse;

import com.jd.jddc.core.convertor.DataRowConvertor;
import com.jd.jddc.core.filter.OperationFilterChain;
import com.jd.jddc.core.handler.LogHandler;
import com.jd.jddc.core.position.LogPositionManager;
import com.taobao.tddl.dbsync.binlog.LogEvent;
import com.taobao.tddl.dbsync.binlog.event.RowsLogEvent;


/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/11.
 * Version:1.0
 */
public class LogEventParse {
    private OperationFilterChain chain;
    // TODO Name Filter
    private LogPositionManager positionManager;
    private String charset;
    private LogHandler handler;
    private String dbName;

    public LogEventParse(){

    }

    public LogEventParse(OperationFilterChain chain, String charset,
                         LogPositionManager positionManager, LogHandler handler){
        this.chain = chain;
        this.positionManager = positionManager;
        this.handler = handler;
        this.charset = charset;
    }

    public void parse(LogEvent event){
        RowsLogEvent rowsEvent = chain.invoke(event);

        positionManager.updateLogPosition(event.getLogPos());
        if(rowsEvent != null && dbName.equals(rowsEvent.getTable().getDbName())) {
            handler.handle(DataRowConvertor.covert(rowsEvent, charset));
        }
    }

    public OperationFilterChain getChain() {
        return chain;
    }

    public void setChain(OperationFilterChain chain) {
        this.chain = chain;
    }

    public LogPositionManager getPositionManager() {
        return positionManager;
    }

    public void setPositionManager(LogPositionManager positionManager) {
        this.positionManager = positionManager;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
