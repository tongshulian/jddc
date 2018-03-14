package com.jd.jddc.core.handler;

import com.jd.jddc.common.DataRow;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/13.
 * Version:1.0
 */
public interface LogHandler {
    boolean handle(DataRow row);
}
