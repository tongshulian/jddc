package com.jd.jddc.connection;

import com.alibaba.otter.canal.parse.driver.mysql.packets.HeaderPacket;
import com.alibaba.otter.canal.parse.driver.mysql.packets.client.QueryCommandPacket;
import com.alibaba.otter.canal.parse.driver.mysql.packets.server.*;
import com.alibaba.otter.canal.parse.driver.mysql.socket.SocketChannel;
import com.alibaba.otter.canal.parse.driver.mysql.utils.PacketManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Dept: 执行数据库语句
 * User: tongshulian
 * Date:2018/2/24.
 * Version:1.0
 */
public class SqlCmdExecutor {

    public static OKPacket update(SocketChannel channel, String updateString) throws IOException{
        QueryCommandPacket cmd = new QueryCommandPacket();
        cmd.setQueryString(updateString);
        byte[] bodyBytes = cmd.toBytes();
        PacketManager.writeBody(channel, bodyBytes);

        byte[] body = PacketManager.readBytes(channel,
                PacketManager.readHeader(channel, 4).getPacketBodyLength());
        if (body[0] < 0) {
            ErrorPacket packet = new ErrorPacket();
            packet.fromBytes(body);
            throw new IOException(packet + "\n with command: " + updateString);
        }

        OKPacket packet = new OKPacket();
        packet.fromBytes(body);
        return packet;
    }

    public static ResultSetPacket query(SocketChannel channel, String queryString) throws IOException{
        QueryCommandPacket cmd = new QueryCommandPacket();
        cmd.setQueryString(queryString);
        byte[] bodyBytes = cmd.toBytes();
        PacketManager.writeBody(channel, bodyBytes);
        byte[] body = readNextPacket(channel);

        if (body[0] < 0) {
            ErrorPacket packet = new ErrorPacket();
            packet.fromBytes(body);
            throw new IOException(packet + "\n with command: " + queryString);
        }

        ResultSetHeaderPacket rsHeader = new ResultSetHeaderPacket();
        rsHeader.fromBytes(body);

        List<FieldPacket> fields = new ArrayList<FieldPacket>();
        for (int i = 0; i < rsHeader.getColumnCount(); i++) {
            FieldPacket fp = new FieldPacket();
            fp.fromBytes(readNextPacket(channel));
            fields.add(fp);
        }

        readEofPacket(channel);

        List<RowDataPacket> rowData = new ArrayList<RowDataPacket>();
        while (true) {
            body = readNextPacket(channel);
            if (body[0] == -2) {
                break;
            }
            RowDataPacket rowDataPacket = new RowDataPacket();
            rowDataPacket.fromBytes(body);
            rowData.add(rowDataPacket);
        }

        ResultSetPacket resultSet = new ResultSetPacket();
        resultSet.getFieldDescriptors().addAll(fields);
        for (RowDataPacket r : rowData) {
            resultSet.getFieldValues().addAll(r.getColumns());
        }
        resultSet.setSourceAddress(channel.getRemoteSocketAddress());

        return resultSet;
    }

    private static byte[] readNextPacket(SocketChannel channel) throws IOException {
        HeaderPacket h = PacketManager.readHeader(channel, 4);
        return PacketManager.readBytes(channel, h.getPacketBodyLength());
    }

    private static boolean readEofPacket(SocketChannel channel) throws IOException {
        byte[] eofBody = readNextPacket(channel);
        EOFPacket packet = new EOFPacket();
        packet.fromBytes(eofBody);
        if (eofBody[0] != -2) {
            throw new IOException("EOF Packet is expected, but packet with field_count=" + eofBody[0] + " is found.");
        }

        return (packet.statusFlag & 0x0008) != 0;
    }
}
