package com.bryer.m2o.running;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhangnanw
 */
@Component
public class Sampling implements Runnable {
    @Qualifier("localOracleSession")
    @Resource
    private Session session;

    private String former = "yyyy-MM-dd HH";

    @Override
    public void run() {
        for (; ; ) {
            try {
                List<String> fieldList = Arrays.asList(
                        "DWDM",
                        "DWMC",
                        "YGDM",
                        "YGMC",
                        "YWGD",
                        "SG",
                        "WD",
                        "ZL",
                        "TJ",
                        "YLDM",
                        "MD",
                        "WD_SC",
                        "WD_ZC",
                        "WD_XC",
                        "YL",
                        "KRJ",
                        "GNQY",
                        "SJ",
                        "SMD");
                List<Entity> datList = session.findAll("TAB_YLGY_SSYW");

                String dataStr = DateUtil.format(new Date(),former);
                Date date = DateUtil.parse(dataStr,former);
                for (Entity entity : datList) {
                    entity.setTableName("TAB_YLGY_LSYGKC");
                    entity.set("LRSJ",date);
                    List<String> removeList = entity.getFieldNames().stream().filter(f -> !fieldList.contains(f)).collect(Collectors.toList());
                    for (String removeField : removeList) {
                        entity.remove(removeField);
                    }
                    session.insertOrUpdate(entity,"DWDM","YGDM","LRSJ");
                }
                System.out.println("导入历史表.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ThreadUtil.sleep(TimeUnit.MINUTES.toMillis(20));
        }
    }
}
