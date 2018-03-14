package com.jd.jddc.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Dept: 心跳检测
 * User: tongshulian
 * Date:2018/3/3.
 * Version:1.0
 */
public class HeartBeatTask {
    private static final Logger log = LoggerFactory.getLogger(HeartBeatTask.class);
    private Connector connector;
    private Timer timer;

    public HeartBeatTask(Connector connector){
        this.connector = connector.fork();
    }

    public void start(){
        String name = String.format("hostName = %s , port = %s , HeartBeatTask",
                connector.getHostName(),
                connector.getPort());
        timer = new Timer(name, true);
        Integer interval = 3;
        timer.schedule(buildTimerTask(), interval * 1000L, interval * 1000L);
        log.info("start heart beat.... ");
    }

    private TimerTask buildTimerTask(){
        return new TimerTask(){

            @Override
            public void run() {
                try {
                     if (!connector.isConnected()) {
                         connector.connect();
                    }

                    Long startTime = System.currentTimeMillis();
                    SqlCmdExecutor.query(connector.getChannel(), "select 1");
                    Long costTime = System.currentTimeMillis() - startTime;
                    log.info("heartBeat 消耗时间为{}ms", costTime);
                } catch (Exception e) {
                    log.error("heartBeat 连接失败！", e);
                }
            }
        };
    }

    public void stop(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
