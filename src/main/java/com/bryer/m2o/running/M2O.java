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
        List<Entity> srcDatList = srcSession.query("SELECT * FROM data_result where stlx='001'");
        for (Entity srcDat : srcDatList) {
            Entity destDat = map(srcDat);
            destSession.insertOrUpdate(destDat,"DWDM","YGDM");
        }
    }

    private Entity map(Entity dat) {
        Entity res = new Entity();
        res.setTableName("TAB_YLGY_SSYW");
        //单位代码
        res.set("DWDM",dat.get("dwdm"));
        //油罐代码
        res.set("YGDM",dat.get("stbm"));
        //时间
        res.set("SJ",dat.get("c_date"));
        //油罐高度
        res.set("YWGD",dat.getDouble("data1"));
        //水高
        res.set("SG",dat.getDouble("data2"));
        //温度
        res.set("WD",dat.getDouble("data3"));
        //上层温度
        res.set("WD_SC",dat.getDouble("data3"));
        //中层温度
        res.set("WD_ZC",dat.getDouble("data3"));
        //下层温度
        res.set("WD_XC",dat.getDouble("data3"));
        //体积
        res.set("TJ",dat.getDouble("data5"));
        //密度
        res.set("MD",dat.getDouble("data7"));
        //视密度
        res.set("SMD",dat.getDouble("data6"));
        //质量
        res.set("ZL",dat.getDouble("data8"));
        //库容积
        res.set("KRJ",dat.getDouble("data9"));
        return res;
    }


}
