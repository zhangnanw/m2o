package com.bryer.m2o;

import com.bryer.m2o.running.M2O;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zhangnan@yansou.org
 */
@SpringBootApplication
public class M2oApplication {
    private static final Logger log = LoggerFactory.getLogger(M2oApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(M2oApplication.class,args);
        log.info("获得Context:{}",context);
        M2O sync = context.getBean(M2O.class);
        log.info("获得同步对象:{}",context);
        sync.run();
        log.info("执行完毕");
    }

}
