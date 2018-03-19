package com.jd.jddc.core.position;

/**
 * Dept: 处理日志位置的服务接口，可以被使用者扩展
 * User: tongshulian
 * Date:2018/2/25.
 * Version:1.0
 */
public interface LogPositionService {
    LogPosition getLogPosition(String hostName);

    /**
     * 新增或更新日志位置
     * @param logPosition
     * @return
     */
    int saveOrUpdateLogPosition(LogPosition logPosition);
}
