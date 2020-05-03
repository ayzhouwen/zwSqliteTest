package com.zw.sqliteTest.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import javax.sql.DataSource;

/**
 * 读写分离并不适合sqlite数据库,读写分离只会造成锁库报错问题,生产单连接+wal就可以了
 */
@Configuration
public class DefaultDatasource {

    @Value("${spring.datasource.driver-class-name}")
    private String driverName;
    @Value("${spring.datasource.url}")
    private String url;
    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverName);
        hikariConfig.setJdbcUrl(url);
        //sqlite能单连接写,不能多个连接并发写,如果业务层数多线程并发写会报Cause: org.sqlite.SQLiteException: [SQLITE_BUSY]  The database file is locked (database is locked)
        //从而导致写入失败,极限性能生产环境推荐一写多读模式+配置sqlite的wal模式,多连接并发读是可以的
        Integer busThreadNum=Runtime.getRuntime().availableProcessors()*2;
        System.out.println("数据库连接数:"+busThreadNum);

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
        hikariConfig.addDataSourceProperty(SQLiteConfig.Pragma.JOURNAL_MODE.pragmaName, SQLiteConfig.JournalMode.WAL );
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }
}
