package com.zw.sqliteTest.config.db;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
/**
 *
 */
@Configuration
@MapperScan("com.zw.sqliteTest.**.mapper*")
public class MybatisPlusConfig {
    /**
     * xml文件所在路径
     */
    @Value("${mybatis-plus.mapper-locations}")
    private String mapperLocations;
    /**
     * 别名扫描包名
     */
    @Value("${mybatis-plus.type-aliases-package}")
    private String typeAliasesPackage;
    /**
     * 是否显示banner
     */
    @Value("${mybatis-plus.global-config.banner}")
    private Boolean banner;
    /**
     * 是否开启转驼峰
     */
    @Value("${mybatis-plus.configuration.map-underscore-to-camel-case}")
    private Boolean mapUnderscoreToCamelCase;
    /**
     * 数据源路由
     */
    @Resource(name = "myRoutingDataSource")
    private DataSource myRoutingDataSource;

    /**
     * 使用MyBatis Plus的sqlSessionFactory代替
     *
     * @return sqlSessionFactory
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(myRoutingDataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setMapUnderscoreToCamelCase(mapUnderscoreToCamelCase);
       // mybatisConfiguration.setObjectWrapperFactory(new MapWrapperFactory());
        sqlSessionFactoryBean.setConfiguration(mybatisConfiguration);
        GlobalConfig config = new GlobalConfig();
        config.setBanner(banner);
        sqlSessionFactoryBean.setGlobalConfig(config);
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{new PaginationInterceptor()});
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 事务配置
     *
     * @return 事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManager() {
        DataSourceTransactionManager tx = new DataSourceTransactionManager();
        tx.setDataSource(myRoutingDataSource);
        return tx;
    }
}
