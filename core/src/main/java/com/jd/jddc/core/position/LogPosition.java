package com.jd.jddc.core.position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/2/25.
 * Version:1.0
 */
public class LogPosition {
    private String currentHost;
    private String currentFileName;
    private long position;
    private Comparable columnValue;

    private List<LogPosition> hisLogPositions = new ArrayList<LogPosition>();

    public LogPosition(String currentHost, String currentFileName, long position){
        this.currentHost = currentHost;
        this.currentFileName = currentFileName;
        this.position = position;
    }

    public String getCurrentHost() {
        return currentHost;
    }

    public void setCurrentHost(String currentHost) {
        this.currentHost = currentHost;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public void setCurrentFileName(String currentFileName) {
        this.currentFileName = currentFileName;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public List<LogPosition> getHisLogPositions() {
        return hisLogPositions;
    }

    public void setHisLogPositions(List<LogPosition> hisLogPositions) {
        this.hisLogPositions = hisLogPositions;
    }

    public Comparable getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(Comparable columnValue) {
        this.columnValue = columnValue;
    }
}
