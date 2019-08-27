package com.bryer.m2o.config;

import cn.hutool.core.util.StrUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author zhangnan@yansou.org
 */
@Configuration
public class DataSourceConfig {
    private static Properties prop = new Properties();

    static {
        File file = null;
        File file1 = new File("database-ip.properties");
        File file2 = new File("/root/database-ip.properties");
        if (file1.exists() && file1.isFile()) {
            file = file1;
        }
        if (file2.exists() && file2.isFile()) {
            file = file2;
        }
        try {
            assert file != null;
            prop.load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Bean
    @Qualifier("localMysqlDataSource")
    public DataSource localMysqlDataSource() throws IOException, PropertyVetoException {
        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass("com.mysql.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://" +
                StrUtil.trim(prop.getProperty("src")) + ":3306/" +
                StrUtil.trim(prop.getProperty("src-name")) + "?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&useSSL=false");
        ds.setUser(prop.getProperty("src-user"));
        ds.setPassword(prop.getProperty("src-pwd"));
        System.out.println("源数据库URL:" + ds.getJdbcUrl());
        return ds;
    }

    @Bean
    @Qualifier("localOracleDataSource")
    public DataSource localOracleDataSource() throws IOException, PropertyVetoException, SQLException {
        String jdbcURL = "jdbc:oracle:thin:@" +
                StrUtil.trim(prop.getProperty("dest")) + ":1521:" +
                StrUtil.trim(prop.getProperty("dest-name"));
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        ds.setUrl(jdbcURL);
        ds.setUsername(prop.getProperty("dest-user"));
        ds.setPassword(prop.getProperty("dest-pwd"));
        System.out.println("目标数据库URL:" + ds.getUrl());
//        OracleConnectionPoolDataSource ds = new OracleConnectionPoolDataSource();
//        ds.setURL("jdbc:oracle:thin:@" + StrUtil.trim(prop.getOrDefault("dest"," 192.168.177.110").toString()) + ":1521:xe");
//        ds.setUser("jygc");
//        ds.setPassword("jygc");
        return ds;
    }
}
