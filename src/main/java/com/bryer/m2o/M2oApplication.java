package com.bryer.m2o;

import cn.hutool.core.thread.ThreadUtil;
import com.bryer.m2o.running.M2O;
import com.bryer.m2o.running.Sampling;
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
        M2O m2o = context.getBean(M2O.class);
        Sampling sampling = context.getBean(Sampling.class);
        ThreadUtil.excAsync(m2o,false);
        ThreadUtil.excAsync(sampling,false);
    }

}
