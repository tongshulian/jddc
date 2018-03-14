package com.jd.jddc.common.cache;

import com.jd.jddc.common.TableMeta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dept:
 * User: tongshulian
 * Date:2018/3/14.
 * Version:1.0
 */
public class TableMetaContext {
    private static Map<String, TableMeta> context = new ConcurrentHashMap<String, TableMeta>();

    public static TableMeta get(String tableName){
        return context.get(tableName);
    }

    public static TableMeta put(String tableName, TableMeta meta){
        return context.put(tableName, meta);
    }
}
