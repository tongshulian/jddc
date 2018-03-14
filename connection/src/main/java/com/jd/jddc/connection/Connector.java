package com.jd.jddc.connection;

import com.alibaba.otter.canal.parse.driver.mysql.socket.SocketChannel;

import java.io.IOException;

/**
 * Dept: DB连接接口
 * User: tongshulian
 * Date:2018/2/11.
 * Version:1.0
 */
public interface Connector {
    void connect() throws IOException;

    void disconnect() throws IOException;

    boolean isConnected();

    void reconnect() throws IOException;

    Connector fork();

    SocketChannel getChannel() throws IOException;

    void setDumping(boolean isDumping);

    boolean isDumping();

    String getHostName();

    int getPort();

    String getDefaultSchema();

    int getSoTimeout();

    int getReceiveBufferSize();

    int getSendBufferSize();

    void setConfig(ConnectorConfig config);
}
