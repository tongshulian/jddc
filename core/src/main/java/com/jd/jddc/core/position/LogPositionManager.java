package com.jd.jddc.core.position;

import com.alibaba.otter.canal.parse.driver.mysql.packets.server.ResultSetPacket;
import com.jd.jddc.connection.Connector;
import com.jd.jddc.connection.SqlCmdExecutor;
import com.jd.jddc.core.buffer.PositionBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dept:日志位置管理类
 * User: tongshulian
 * Date:2018/2/25.
 * Version:1.0
 */
public class LogPositionManager {
    private static final Logger log = LoggerFactory.getLogger(LogPositionManager.class);
    public static final long BINLOG_START_OFFEST = 4L;
    private int flushSize = 128;
    private PositionBuffer positionBuffer;
    private LogPositionService positionService = new LogPositionLocalService();

    public LogPositionManager(){
        positionBuffer = new PositionBuffer(flushSize);
    }

    public LogPositionManager(int flushSize){
        positionBuffer = new PositionBuffer(flushSize);
    }

    public LogPosition findStartLogPosition(Connector connector, int offset){
        LogPosition logPosition = null;

        // 外部未提供日志位置服务接口的实现类，则使用LogPositionLocalService
        if(positionService == null){
            positionService = new LogPositionLocalService();
        }

        // 从日志位置服务接口获取日志位置。
        logPosition = positionService.getLogPosition(connector.getHostName()
         + ":" + connector.getPort());

        // 日志位置获取的服务为空，则从连接中读取
        if(logPosition == null){
            // 获取日志目前最后一条记录的位置
            logPosition = findEndPosition(connector);

            // 有偏移量，则根据偏移量计算日志记录开始位置;否则位置为日志第一条记录的位置
            if(offset > 0){
                long startPosition = logPosition.getPosition() - offset;
                logPosition.setPosition(startPosition > 0 ? startPosition : BINLOG_START_OFFEST);
            }else{
                logPosition.setPosition(BINLOG_START_OFFEST);
            }
        }

        // 缓冲日志开始位置
        positionBuffer.addLogPosition(logPosition, positionService);
        return logPosition;
    }

    /**
     * 查询当前的binlog位置
     */
    private LogPosition findEndPosition(Connector connector) {
        try {
            ResultSetPacket packet = SqlCmdExecutor.query(connector.getChannel(), "show master status");
            List<String> fields = packet.getFieldValues();
            if (CollectionUtils.isEmpty(fields)) {
                throw new RuntimeException("command : 'show master status' has an error! pls check. you need (at least one of) the SUPER,REPLICATION CLIENT privilege(s) for this operation");
            }

            LogPosition endPosition = new LogPosition(connector.getHostName() + ":" + connector.getPort(),
                    fields.get(0), Long.valueOf(fields.get(1)));
            return endPosition;
        } catch (IOException e) {
            throw new RuntimeException("command : 'show master status' has an error!", e);
        }
    }

    public void addLogPosition(LogPosition logPosition){
        positionBuffer.addLogPosition(logPosition, positionService);
    }

    public void updateLogPosition(long position){
        positionBuffer.addLogPosition(position, positionService);
    }

    public LogPositionService getPositionService() {
        return positionService;
    }

    public void setPositionService(LogPositionService positionService) {
        this.positionService = positionService;
    }
}
