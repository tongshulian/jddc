package com.jd.jddc.core.sync;

import com.jd.jddc.connection.ConnectorConfig;
import com.jd.jddc.connection.factory.DefaultConnectorFactory;
import org.junit.Test;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/13.
 * Version:1.0
 */
public class BinLogSyncTest {

    @Test
    public void syncTest(){
        DefaultConnectorFactory factory = new DefaultConnectorFactory();
        ConnectorConfig config = new ConnectorConfig("192.168.147.87", 3306, "jdlottery", "jdlottery", "ptrip");
        factory.setConfig(config);
        BinLogSync sync = new BinLogSync();
        sync.setFactory(factory);
        sync.init();
    }
}
