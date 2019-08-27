package com.bryer.m2o;

import com.bryer.m2o.running.TableSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zhangnan@yansou.org
 */
@SpringBootApplication
public class TabsyncApplication {
    private static final Logger log = LoggerFactory.getLogger(TabsyncApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TabsyncApplication.class,args);
        log.info("获得Context:{}",context);
        TableSync sync = context.getBean(TableSync.class);
        log.info("获得同步对象:{}",context);
        sync.run();
        log.info("执行完毕");
    }

}
