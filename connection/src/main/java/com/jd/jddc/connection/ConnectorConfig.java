package com.jd.jddc.connection;

/**
 * Dept: 连接配置类
 * User: tongshulian
 * Date:2018/2/11.
 * Version:1.0
 */
public class ConnectorConfig {
    String hostName;
    int port;
    private String username;
    private String password;
//    private CharsetEnum defaultCharset;
    private String defaultSchema;
    private int soTimeout;
    private int receiveBufferSize;
    private int sendBufferSize;

    public ConnectorConfig(String hostName, int port, String userName, String password,
                           String defaultSchema){
        this.hostName = hostName;
        this.port = port;
        this.username = userName;
        this.password = password;
        this.defaultSchema = defaultSchema;
    }

    public ConnectorConfig(){

    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }
}
