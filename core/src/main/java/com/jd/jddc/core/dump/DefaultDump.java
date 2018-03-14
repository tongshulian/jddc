package com.jd.jddc.core.dump;

import com.alibaba.otter.canal.parse.driver.mysql.packets.HeaderPacket;
import com.alibaba.otter.canal.parse.driver.mysql.packets.client.BinlogDumpCommandPacket;
import com.alibaba.otter.canal.parse.driver.mysql.utils.PacketManager;
import com.jd.jddc.connection.Connector;
import com.jd.jddc.connection.SqlCmdExecutor;
import com.jd.jddc.core.parse.LogEventParse;
import com.jd.jddc.core.position.LogPosition;
import com.taobao.tddl.dbsync.binlog.LogContext;
import com.taobao.tddl.dbsync.binlog.LogDecoder;
import com.taobao.tddl.dbsync.binlog.LogEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/2/25.
 * Version:1.0
 */
public class DefaultDump implements Dump {
    private static final Logger log = LoggerFactory.getLogger(DefaultDump.class);
    private Connector connector;
    private long slaveId;

    public DefaultDump(Connector connector, long slaveId){
        this.connector = connector;
        this.slaveId = slaveId;
    }

    @Override
    public void seek(LogPosition position, LogEventParse parse) throws IOException {
        updateSettings();

        sendBinlogDump(position.getCurrentFileName(), position.getPosition());

        DirectLogFetcher fetcher = new DirectLogFetcher(connector.getReceiveBufferSize());
        fetcher.start(connector.getChannel());

        LogDecoder decoder = new LogDecoder();
        decoder.handle(LogEvent.ROTATE_EVENT);
        decoder.handle(LogEvent.FORMAT_DESCRIPTION_EVENT);
        decoder.handle(LogEvent.QUERY_EVENT);
        decoder.handle(LogEvent.XID_EVENT);

        LogContext context = new LogContext();
        sink(fetcher, decoder, context, parse);
    }

    private void sink(DirectLogFetcher fetcher, LogDecoder decoder,
                          LogContext context, LogEventParse logParse) throws IOException{
        while (fetcher.fetch()) {
            LogEvent event = decoder.decode(fetcher, context);

            if (event == null) {
                log.error("解析日志异常，日志内容为空，charset=" + logParse.getCharset());
            }

            logParse.parse(event);
        }
    }

    @Override
    public void dump(LogPosition position, LogEventParse parse) throws IOException {
        updateSettings();

        sendBinlogDump(position.getCurrentFileName(), position.getPosition());

        DirectLogFetcher fetcher = new DirectLogFetcher(connector.getReceiveBufferSize());
        fetcher.start(connector.getChannel());
        LogDecoder decoder = new LogDecoder(LogEvent.UNKNOWN_EVENT, LogEvent.ENUM_END_EVENT);
        LogContext context = new LogContext();
        sink(fetcher, decoder, context, parse);
    }

    @Override
    public void dump(long timestamp, LogEventParse parse) throws IOException {

    }

    private void sendBinlogDump(String binlogFilename, Long binlogPosition) throws IOException {
        BinlogDumpCommandPacket binlogDumpCmd = new BinlogDumpCommandPacket();
        binlogDumpCmd.binlogFileName = binlogFilename;
        binlogDumpCmd.binlogPosition = binlogPosition;
        binlogDumpCmd.slaveServerId = this.slaveId;
        byte[] cmdBody = binlogDumpCmd.toBytes();

        log.info("COM_BINLOG_DUMP with position:{}", binlogDumpCmd);
        HeaderPacket binlogDumpHeader = new HeaderPacket();
        binlogDumpHeader.setPacketBodyLength(cmdBody.length);
        binlogDumpHeader.setPacketSequenceNumber((byte) 0x00);
        PacketManager.writePkg(connector.getChannel(), binlogDumpHeader.toBytes(), cmdBody);
        connector.setDumping(true);
    }

    private void update(String updateString, String warning){
        try {
            SqlCmdExecutor.update(connector.getChannel(), updateString);
        } catch (Exception e) {
            log.warn(warning, e);
        }
    }

    // ====================== help method ====================
    /**
     * the settings that will need to be checked or set:<br>
     * <ol>
     * <li>wait_timeout</li>
     * <li>net_write_timeout</li>
     * <li>net_read_timeout</li>
     * </ol>
     *
     * @throws IOException
     */
    private void updateSettings() throws IOException {
        update("set wait_timeout=9999999", "update wait_timeout failed");
        update("set net_write_timeout=1800", "update net_write_timeout failed");
        update("set net_read_timeout=1800", "update net_read_timeout failed");

        // 设置服务端返回结果时不做编码转化，直接按照数据库的二进制编码进行发送，
        // 由客户端自己根据需求进行编码转化
        update("set names 'binary'", "update names failed");

        // mysql5.6针对checksum支持需要设置session变量
        // 如果不设置会出现错误： Slave can not handle replication events with the
        // checksum that master is configured to log
        // 但也不能乱设置，需要和mysql server的checksum配置一致，不然RotateLogEvent会出现乱码
        update("set @master_binlog_checksum= '@@global.binlog_checksum'",
                "update master_binlog_checksum failed");

        // 参考:https://github.com/alibaba/canal/issues/284
        // mysql5.6需要设置slave_uuid避免被server kill链接
        try {
            SqlCmdExecutor.update(connector.getChannel(), "set @slave_uuid=uuid()");
        } catch (Exception e) {
            if (!StringUtils.contains(e.getMessage(), "Unknown system variable")) {
                log.warn("update slave_uuid failed", e);
            }
        }

        // mariadb针对特殊的类型，需要设置session变量
        update("SET @mariadb_slave_capability='" + LogEvent.MARIA_SLAVE_CAPABILITY_MINE + "'",
                "update mariadb_slave_capability failed");
    }

    public Connector getConnector() {
        return connector;
    }

    public long getSlaveId() {
        return slaveId;
    }
}
