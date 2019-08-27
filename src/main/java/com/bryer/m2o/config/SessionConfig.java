package com.bryer.m2o.config;

import cn.hutool.db.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author zhangnan@yansou.org
 */
@Configuration
public class SessionConfig {

    private final DataSource localMysqlDataSource;
    private final DataSource localOracleDataSource;

    public SessionConfig(@Qualifier("localMysqlDataSource") DataSource localMysqlDataSource,@Qualifier("localOracleDataSource") DataSource localOracleDataSource) {
        this.localMysqlDataSource = localMysqlDataSource;
        this.localOracleDataSource = localOracleDataSource;
    }

    @Bean
    @Qualifier("localMysqlSession")
    Session localMysqlSession() {
        System.out.println("源会话:" + localMysqlDataSource);
        return Session.create(localMysqlDataSource);
    }

    @Bean
    @Qualifier("localOracleSession")
    Session localOracleSession() {
        System.out.println("目标会话:" + localOracleDataSource);
        return new Session(localOracleDataSource,new MyOracleDialect());
    }
}
