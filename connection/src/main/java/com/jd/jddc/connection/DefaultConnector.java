package com.jd.jddc.connection;

import com.alibaba.otter.canal.parse.driver.mysql.MysqlConnector;
import com.alibaba.otter.canal.parse.driver.mysql.socket.SocketChannel;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Dept: 实现的基于Mysql的连接
 * User: tongshulian
 * Date:2018/3/11.
 * Version:1.0
 */
public class DefaultConnector implements Connector{
    private final MysqlConnector connector = new MysqlConnector();
    private int soTimeout;
    private int receiveBufferSize;
    private int sendBufferSize;
    private volatile boolean isInitialize = false;
    // TODO CharsetEnum
//    private CharsetEnum charsetNumber;
    private ConnectorConfig config;

    public DefaultConnector(){

    }

    public DefaultConnector(String hostName, int port, String userName, String password){
        config = new ConnectorConfig(hostName, port, userName, password, null);
        init();
    }

    public void init(){
        if(config == null){
            throw new RuntimeException("DefaultConnector 初始化失败，配置config为空！");
        }
        InetSocketAddress address = new InetSocketAddress(config.getHostName(), config.getPort());
        connector.setAddress(address);
        connector.setUsername(config.getUsername());
        connector.setPassword(config.getPassword());
    }

    @Override
    public void connect() throws IOException {
        if(!isInitialize){
            init();
            isInitialize = true;
        }

        String defaultSchema = config.getDefaultSchema();
        if(StringUtils.isNotBlank(config.getDefaultSchema())){
            connector.setDefaultSchema(defaultSchema);
        }
        if(soTimeout > 0){
            connector.setSoTimeout(soTimeout);
        }
        if(receiveBufferSize > 0){
            connector.setReceiveBufferSize(receiveBufferSize);
        }
        if(sendBufferSize > 0){
            connector.setSendBufferSize(sendBufferSize);
        }

        connector.connect();
    }

    @Override
    public Connector fork() {
        DefaultConnector defaultConnector = new DefaultConnector();

        defaultConnector.setConfig(config);
        return defaultConnector;
    }

    @Override
    public void disconnect() throws IOException {
        connector.disconnect();
    }

    @Override
    public boolean isConnected() {
        return connector.isConnected();
    }

    @Override
    public void reconnect() throws IOException {
        connector.reconnect();
    }

    @Override
    public SocketChannel getChannel() {
        return connector.getChannel();
    }

    @Override
    public void setDumping(boolean isDumping) {
        connector.setDumping(isDumping);
    }

    @Override
    public String getHostName() {
        if(config == null){
            return  "";
        }
        return config.getHostName();
    }

    @Override
    public int getPort() {
        if(config == null){
            return -1;
        }
        return config.getPort();
    }

    @Override
    public boolean isDumping() {
        return false;
    }

    public String getDefaultSchema() {
        if(config == null){
            return null;
        }
        return config.getDefaultSchema();
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

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "DefaultConnector[" + config.getHostName() + ":" + config.getPort()
                + "/" + config.getDefaultSchema() + ", isInitialize=" + isInitialize +
                "]";
    }
}
