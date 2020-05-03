package com.zw.sqliteTest.config.db;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DBContextHolder {

    private static final ThreadLocal<DBTypeEnum> contextHolder = new ThreadLocal<>();

    private static final AtomicInteger counter = new AtomicInteger(-1);

    public static void set(DBTypeEnum dbType) {
        contextHolder.set(dbType);
    }

    public static DBTypeEnum get() {
        return contextHolder.get();
    }

    public static void workMaster() {
        set(DBTypeEnum.WORKDB_MASTER);

            log.info("数据源切换至master");

    }

    public static void workSlave() {
        set(DBTypeEnum.WORKDB_SLAVE);

            log.info("数据源切换至slave");

    }

}
