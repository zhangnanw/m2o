package com.bryer.m2o.running;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * @author zhangnanw@yansou.org
 */
@Slf4j
@Component
public class M2O implements Runnable {
    @Qualifier("localMysqlSession")
    @Resource
    private Session srcSession;
    @Qualifier("localOracleSession")
    @Resource
    private Session destSession;


    @Override
    public void run() {
        for (; ; ) {
            try {
                importData();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ThreadUtil.sleep(200);
            }
        }
    }

    private void importData() throws SQLException {
        List<Entity> srcDatList = srcSession.query("SELECT * FROM data_result");
        for (Entity srcDat : srcDatList) {
            Entity destDat = map(srcDat);
            destSession.insertOrUpdate(destDat,"DWDM","YGDM");
        }
    }

    private Entity map(Entity dat) {
        Entity res = new Entity();
        res.setTableName("TAB_YLGY_SSYW");
        res.set("DWDM",dat.get("dwdm"));
        res.set("YGDM",dat.get("stbm"));
        res.set("SJ",dat.get("c_date"));
        res.set("YWGD",dat.getDouble("data1"));
        res.set("SG",dat.getDouble("data2"));
        res.set("WD",dat.getDouble("data3"));
        res.set("WD_SC",dat.getDouble("data3"));
        res.set("WD_ZC",dat.getDouble("data3"));
        res.set("WD_XC",dat.getDouble("data3"));
        res.set("TJ",dat.getDouble("data5"));
        res.set("MD",dat.getDouble("data7"));
        res.set("ZL",dat.getDouble("data8"));
        res.set("KRJ",dat.getDouble("data9"));
        return res;
    }
}
