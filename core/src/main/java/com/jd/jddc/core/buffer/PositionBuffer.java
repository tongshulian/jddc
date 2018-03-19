package com.jd.jddc.core.buffer;

import com.jd.jddc.core.position.LogPosition;
import com.jd.jddc.core.position.LogPositionService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dept: 日志位置缓冲类
 * User: tongshulian
 * Date:2018/3/19.
 * Version:1.0
 */
public class PositionBuffer {
    private AtomicInteger flushSize;
    private int size;
    private volatile LogPosition buffer = null;

    public PositionBuffer(int size){
        this.size = size;
        flushSize = new AtomicInteger(size);
    }

    public LogPosition addLogPosition(LogPosition position, LogPositionService positionService){
        buffer = position;
        if(flushSize.decrementAndGet() == 0){
            // flush
            positionService.saveOrUpdateLogPosition(buffer);
            flushSize.compareAndSet(0, size);
        }

        return buffer;
    }

    public LogPosition addLogPosition(long position, LogPositionService positionService){
        if(buffer == null){
            return null;
        }

        buffer.setPosition(position);
        addLogPosition(buffer, positionService);

        return buffer;
    }
}
