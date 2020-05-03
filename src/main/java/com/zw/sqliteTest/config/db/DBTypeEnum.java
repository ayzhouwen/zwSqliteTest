package com.zw.sqliteTest.config.db;

/**
 * 数据源类型枚举
 * @author
 * @since 1.0
 */
public enum DBTypeEnum {
    //正常业务数据库主从,缓存数据库主从
    WORKDB_MASTER, WORKDB_SLAVE,CACHE_MASTER,CACHE_SLAVE;
}