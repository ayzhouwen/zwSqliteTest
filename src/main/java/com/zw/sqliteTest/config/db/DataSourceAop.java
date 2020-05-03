package com.zw.sqliteTest.config.db;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
/**
 * 读写分离并不适合sqlite数据库,读写分离只会造成锁库报错问题,生产单连接+wal就可以了
 */
//@Aspect
//@Component
@Slf4j
public class DataSourceAop {

    /**
     * 使用从库查询
     */
    @Pointcut("@annotation(com.zw.sqliteTest.config.db.WorkSlave) " +
            "|| execution(* com.zw.sqliteTest..service..*.select*(..)) " +
            "|| execution(* com.zw.sqliteTest..service..*.list*(..)) " +
            "|| execution(* com.zw.sqliteTest..service..*.query*(..)) " +
            "|| execution(* com.zw.sqliteTest..service..*.find*(..)) " +
            "|| execution(* com.zw.sqliteTest..service..*.get*(..))")
    public void readPointcut() {
        log.info("readPointcut");
    }

    /**
     * 使用主库插入更新
     */
    @Pointcut("@annotation(com.zw.sqliteTest.config.db.WorkMaster) " +
            "|| execution(* com.zw.sqliteTest..service..*.login(..)) " +
            "|| execution(* com.zw.sqliteTest..service..*.insert*(..)) " +
            "|| execution(* com.zw.sqliteTest..service..*.add*(..)) " +
            "|| execution(* com.zw.sqliteTest..service..*.update*(..)) " +
            "|| execution(* com.zw.sqliteTest..service..*.edit*(..)) " +
            "|| execution(* com.zw.sqliteTest..service..*.delete*(..)) " +
            "|| execution(* com.zw.sqliteTest..service..*.remove*(..))")
    public void writePointcut() {
        log.info("writePointcut");
    }

    @Before("readPointcut()")
    public void read() {
        DBContextHolder.workSlave();
    }

    @Before("writePointcut()")
    public void write() {
        DBContextHolder.workMaster();
    }

}
