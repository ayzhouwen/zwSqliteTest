package com.zw.sqliteTest.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 *  sqlite 一写多读,性能起飞
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverName;
    @Value("${spring.datasource.url}")
    private String url;

    /**
     * 业务主数据库
     * @return DataSource
     */
    @Bean("masterDataSource")
    @ConfigurationProperties("spring.datasource.master")
    public DataSource masterDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverName);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setMaximumPoolSize(1);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        //在数据源设置打开模式属性值然后在传递给jdbc
        SQLiteConfig config= new SQLiteConfig();
        config.setOpenMode(SQLiteOpenMode.OPEN_URI);
        config.setOpenMode(SQLiteOpenMode.FULLMUTEX);
        config.setBusyTimeout(10000);
        hikariConfig.setPoolName("springHikariCP");
        hikariConfig.addDataSourceProperty(SQLiteConfig.Pragma.OPEN_MODE.pragmaName, config.getOpenModeFlags());
        hikariConfig.addDataSourceProperty(SQLiteConfig.Pragma.JOURNAL_MODE.pragmaName, SQLiteConfig.JournalMode.WAL );
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }



    /**
     * 从数据库
     * @return DataSource
     */
    @Bean("slaveDataSource")
    @ConfigurationProperties("spring.datasource.slave")
    public DataSource slaveDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverName);
        hikariConfig.setJdbcUrl(url);
        Integer busThreadNum=Runtime.getRuntime().availableProcessors();
        hikariConfig.setMaximumPoolSize(2*busThreadNum);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        //在数据源设置打开模式属性值然后在传递给jdbc
        SQLiteConfig config= new SQLiteConfig();
        config.setOpenMode(SQLiteOpenMode.OPEN_URI);
        config.setOpenMode(SQLiteOpenMode.READWRITE);
        config.setOpenMode(SQLiteOpenMode.NOMUTEX);
        config.setBusyTimeout(10000);
        hikariConfig.setPoolName("springHikariCP");
        hikariConfig.addDataSourceProperty(SQLiteConfig.Pragma.OPEN_MODE.pragmaName, config.getOpenModeFlags());
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }

    /**
     * 数据源路由
     * @param masterDataSource 主数据库
     * @param slaveDataSource 从数据库
     * @return DataSource
     */
    @Bean
    public DataSource myRoutingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                          @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DBTypeEnum.WORKDB_MASTER, masterDataSource);
        targetDataSources.put(DBTypeEnum.WORKDB_SLAVE, slaveDataSource);
        MyRoutingDataSource myRoutingDataSource = new MyRoutingDataSource();
        myRoutingDataSource.setDefaultTargetDataSource(masterDataSource);
        myRoutingDataSource.setTargetDataSources(targetDataSources);
        return myRoutingDataSource;
    }


}