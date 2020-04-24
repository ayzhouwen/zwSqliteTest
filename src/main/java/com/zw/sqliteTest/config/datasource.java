package com.zw.sqliteTest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import javax.sql.DataSource;

@Configuration
public class datasource {

    @Value("${spring.datasource.driver-class-name}")
    private String driverName;
    @Value("${spring.datasource.url}")
    private String url;
    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverName);
        hikariConfig.setJdbcUrl(url);
        //因为sqlite的并发模型机制,这里必须设置为1,因为无论你的参数设置成NOMUTEX还是FULLMUTEX,并发写入下都会出现Caused by: org.sqlite.SQLiteException: [SQLITE_LOCKED]  A table in the database is locked (database table is locked: student)
        //除非自己去实现连接池,自定义写入线程池,然后能够做到不通的写入线程执行都是不同的连接,或者wal模式,不推荐多写入模型,推荐一写多读模型,或者在参考wal模式
       //说的更直白就是一个线程操作不同的sqlite连接直接报错SQLITE_LOCKED,正常情况下sqlite连接与线程是1对1的关系,可以一个连接对应多个线程,但是不能一个线程对应多个连接
        hikariConfig.setMaximumPoolSize(1);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        //在数据源设置打开模式属性值然后在传递给jdbc
        SQLiteConfig config= new SQLiteConfig();
        config.setOpenMode(SQLiteOpenMode.OPEN_URI);
        config.setOpenMode(SQLiteOpenMode.READWRITE);
        config.setOpenMode(SQLiteOpenMode.SHAREDCACHE);
        config.setOpenMode(SQLiteOpenMode.NOMUTEX);
        hikariConfig.setPoolName("springHikariCP");
        hikariConfig.addDataSourceProperty(SQLiteConfig.Pragma.OPEN_MODE.pragmaName, config.getOpenModeFlags());
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }
}
