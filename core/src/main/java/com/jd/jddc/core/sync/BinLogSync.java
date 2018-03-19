package com.jd.jddc.core.sync;

import com.jd.jddc.common.DataRow;
import com.jd.jddc.connection.Connector;
import com.jd.jddc.connection.HeartBeatTask;
import com.jd.jddc.connection.factory.ConnectorFactory;
import com.jd.jddc.core.TableMetaService;
import com.jd.jddc.core.dump.DefaultDump;
import com.jd.jddc.core.dump.Dump;
import com.jd.jddc.core.filter.InsertFilter;
import com.jd.jddc.core.filter.OperationFilterChain;
import com.jd.jddc.core.filter.UpdateFilter;
import com.jd.jddc.core.handler.LogHandler;
import com.jd.jddc.core.parse.LogEventParse;
import com.jd.jddc.core.position.LogPosition;
import com.jd.jddc.core.position.LogPositionManager;
import com.jd.jddc.core.position.LogPositionService;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dept: 第一版暂不支持事务性的同步，只监听数据库中的一张表，只同步insert、update操作
 * 一台机器限制，最多能够启动10个BinLogSync。
 * User: tongshulian
 * Date:2018/2/25.
 * Version:1.0
 */
public class BinLogSync {
    private static final Logger log = LoggerFactory.getLogger(BinLogSync.class);

    private static final AtomicLong slaveId = new AtomicLong(System.currentTimeMillis());
    public static int MAX_EXISTS = 10;
    private final AtomicInteger exists = new AtomicInteger(0);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final LogPositionManager logPositionManager = new LogPositionManager();

    private HeartBeatTask heartBeatTask;
    private LogPosition startPosition;
    private LogPositionService positionService;
    private ConnectorFactory factory;
    private int logPositionOffset;
    private LogEventParse parse;
    private OperationFilterChain chain;
    private String charset = "utf-8";
    private LogHandler handler;

    public BinLogSync(){
        chain = new OperationFilterChain();
        chain.addFilter(new InsertFilter());
        chain.addFilter(new UpdateFilter());

        handler = new LogHandler() {
            @Override
            public boolean handle(DataRow row) {
                System.out.println(row);
                return true;
            }
        };
    }

    public void init(){
        log.info("初始化");
        if(!running.compareAndSet(false, true) || exists.incrementAndGet() > MAX_EXISTS){
            log.error("重复初始化。。。。。。");
            return;
        }

        logPositionManager.setPositionService(positionService);
        parse = new LogEventParse(chain, charset, logPositionManager, handler);
        final Connector connector = factory.getConnector();
        parse.setDbName(connector.getDefaultSchema());
        final Dump dump = new DefaultDump(connector, slaveId.addAndGet(1));

        String threadName = "【binLog sync(" + slaveId.get() + ")】";
        Thread dumpThread = new Thread(threadName){
            public void run() {
                MDC.put("threadName", this.getName());
                while (running.get()) {
                    try {
                        // 启动心跳检测
                        heartBeatTask = new HeartBeatTask(connector);
                        heartBeatTask.start();

                        // 建立连接
                        connector.connect();
                        // 获取历史同步位置
                        startPosition = logPositionManager.findStartLogPosition(connector,
                                logPositionOffset);

                        // 获取表配置信息
                        TableMetaService tableMetaService = new TableMetaService(connector);
                        tableMetaService.fillContext();

                        // 获取历史同步位置时可能有状态
                        connector.reconnect();

                        // 开始同步
                        dump.dump(startPosition, parse);
                    } catch (Exception e) {
                        log.error(this.getName() + " 同步异常！", e);
                    } finally {
                        // 重新置为中断状态
                        Thread.interrupted();
                        // 关闭一下链接
                        if(connector.isConnected()){
                            try {
                                connector.disconnect();
                            } catch (IOException e) {
                                log.error(this.getName() + " 同步完后，关闭连接异常！", e);
                            }
                        }
                        heartBeatTask.stop();
                    }

                    if (running.get()) {
                        // sleep一段时间再进行重试
                        try {
                            Thread.sleep(10000 + RandomUtils.nextInt(10000));
                        } catch (InterruptedException e) {
                        }
                    }
                }
                MDC.remove("threadName");
            }
        };

        dumpThread.start();
        try {
            dumpThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void destroy(){
        // 关闭当前
        running.compareAndSet(true, false);

        // 关闭心跳
        heartBeatTask.stop();

        // 关闭连接
        if(factory != null){
            factory.destroy();
        }
    }

    public ConnectorFactory getFactory() {
        return factory;
    }

    public void setFactory(ConnectorFactory factory) {
        this.factory = factory;
    }

    public boolean isRunning() {
        return running.get();
    }

    public long getSlaveId() {
        return slaveId.get();
    }

    public int getLogPositionOffset() {
        return logPositionOffset;
    }

    public void setLogPositionOffset(int logPositionOffset) {
        this.logPositionOffset = logPositionOffset;
    }

    public void setPositionService(LogPositionService positionService) {
        logPositionManager.setPositionService(positionService);
    }

    public LogPositionService getPositionService() {
        return positionService;
    }

    public OperationFilterChain getChain() {
        return chain;
    }

    public void setChain(OperationFilterChain chain) {
        this.chain = chain;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public LogHandler getHandler() {
        return handler;
    }

    public void setHandler(LogHandler handler) {
        this.handler = handler;
    }
}
