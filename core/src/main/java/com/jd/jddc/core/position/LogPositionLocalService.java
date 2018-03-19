package com.jd.jddc.core.position;

import java.io.*;
import java.net.URL;

/**
 * Dept: 通过使用本地文件实现存储和读取日志位置对象服务类
 * User: tongshulian
 * Date:2018/2/25.
 * Version:1.0
 */
public class LogPositionLocalService implements LogPositionService{
    @Override
    public LogPosition getLogPosition(String hostName) {
        File file = getFile(hostName);
        if(file.exists()){
            ObjectInputStream reader = null;
            try {
                reader = new ObjectInputStream(new FileInputStream(file));
                return (LogPosition) reader.readObject();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }finally {
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                }
            }
        }

        return null;
    }

    private File getFile(String hostName){
        URL url = this.getClass().getResource("");
        File file = new File(url.getPath() + hostName.replace(":", "-") + ".log");

        return file;
    }

    @Override
    public int saveOrUpdateLogPosition(LogPosition logPosition) {
        File file = getFile(logPosition.getCurrentHost());
        ObjectOutputStream outputStream = null;
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(logPosition);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if(outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {

            }
        }

        return 1;
    }

    public static void main(String[] args){
        LogPositionLocalService service = new LogPositionLocalService();
        LogPosition position = new LogPosition("198.162.154.1:66", "mysql0001", System.currentTimeMillis());
        service.saveOrUpdateLogPosition(position);
        System.out.println(service.getLogPosition(position.getCurrentHost()));
        position.setPosition(System.currentTimeMillis());
        service.saveOrUpdateLogPosition(position);
        System.out.println(service.getLogPosition(position.getCurrentHost()));
        service.getLogPosition("");
    }
}
