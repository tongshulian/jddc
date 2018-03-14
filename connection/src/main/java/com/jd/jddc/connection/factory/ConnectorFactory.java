package com.jd.jddc.connection.factory;

import com.jd.jddc.connection.Connector;
import com.jd.jddc.connection.ConnectorConfig;

/**
 * Dept: 连接工厂抽象接口
 * User: tongshulian
 * Date:2018/2/11.
 * Version:1.0
 */
public interface ConnectorFactory {

    /**
     * 获取连接
     * @return
     */
    Connector getConnector();

    void destroy();

    String getHostName();

    int getPort();

    String getUserName();

    String getSchema();
}
