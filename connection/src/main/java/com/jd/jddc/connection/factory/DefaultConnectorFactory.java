package com.jd.jddc.connection.factory;

import com.jd.jddc.connection.ConnectorConfig;
import com.jd.jddc.connection.DefaultConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Dept: 生成默认连接的工厂类，连接是复用的。
 * User: tongshulian
 * Date:2018/2/11.
 * Version:1.0
 */
public class DefaultConnectorFactory implements ConnectorFactory{
    private static final Logger log = LoggerFactory.getLogger(DefaultConnectorFactory.class);
    private static final DefaultConnector connector = new DefaultConnector();
    private ConnectorConfig config;

    public DefaultConnectorFactory(){

    }

    public DefaultConnector getConnector(){
        return connector;
    }

    public String getHostName() {
        if(config == null){
            return null;
        }

        return config.getHostName();
    }

    public int getPort() {
        if(config == null){
            return -1;
        }

        return config.getPort();
    }

    public String getUserName() {
        if(config == null){
            return null;
        }

        return config.getUsername();
    }

    public String getSchema(){
        if(config == null){
            return null;
        }

        return config.getDefaultSchema();
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
        connector.setConfig(config);
    }

    public void destroy(){
        try {
            connector.disconnect();
        } catch (IOException e) {
            log.error("释放连接时出错！", e);
        }
    }
}
