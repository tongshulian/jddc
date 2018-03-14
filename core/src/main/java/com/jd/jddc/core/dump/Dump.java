package com.jd.jddc.core.dump;

import com.jd.jddc.core.parse.LogEventParse;
import com.jd.jddc.core.position.LogPosition;

import java.io.IOException;

/**
 * Dept: 拉取数据
 * User: tongshulian
 * Date:2018/2/24.
 * Version:1.0
 */
public interface Dump {
    /**
     * 用于快速数据查找,和dump的区别在于，seek会只给出部分的数据
     */
    public void seek(LogPosition position, LogEventParse parse) throws IOException;

    public void dump(LogPosition position, LogEventParse parse) throws IOException;

    public void dump(long timestamp, LogEventParse parse) throws IOException;
}
