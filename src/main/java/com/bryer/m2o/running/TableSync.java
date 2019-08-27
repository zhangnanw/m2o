package com.bryer.m2o.running;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangnan@yansou.org
 */
@Component
public class TableSync implements Runnable {

    private final static String TOP = "JYGC";
    private final Session srcSession;
    private final Session destSession;

    public TableSync(@Qualifier("localMysqlSession") Session srcSession,@Qualifier("localOracleSession") Session destSession) {
        this.srcSession = srcSession;
        this.destSession = destSession;
    }


    @Override
    public void run() {
        System.out.println("同步开始");
        AtomicInteger syncThreadSeq = new AtomicInteger();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3,r -> {
            Thread thread = new Thread(r);
            thread.setName("[TAB TO TAB:" + syncThreadSeq.incrementAndGet() + "]");
            return thread;
        });
        File incFile1 = new File("incrementTab.txt");
        File incFile2 = new File("/root/incrementTab.txt");
        File updFile1 = new File("updateTab.txt");
        File updFile2 = new File("/root/updateTab.txt");

        Runnable incRun = () -> {
            for (; ; ) {
                try {
                    if (!allInsertSyncTable(incFile1)) {
                        allInsertSyncTable(incFile2);
                    }
                    ThreadUtil.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    ThreadUtil.sleep(10 * 1000);
                }
            }
        };
        Runnable updRun = () -> {
            for (; ; ) {
                try {
                    if (!allUpdateSyncTable(updFile1)) {
                        allUpdateSyncTable(updFile2);
                    }
                    ThreadUtil.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    ThreadUtil.sleep(10 * 1000);
                }
            }
        };
        //增量同步
        executor.execute(incRun);
        //更新同步
        executor.execute(updRun);
    }

    /**
     * @param file 需要的文件
     * @return 文件是否存在
     */
    private boolean allUpdateSyncTable(File file) throws SQLException {
        if (file.exists() && file.isFile()) {
            List<String> lines = FileUtil.readLines(file,Charset.defaultCharset());
            for (String line : lines) {
                line = StrUtil.trim(line);
                if (line.startsWith("#")) {
                    continue;
                }
                if (StrUtil.isNotEmpty(line)) {
                    updateSyncTable(line);
                }
            }
            return true;
        } else {
            return false;
        }

    }

    private boolean allInsertSyncTable(File file) throws SQLException {
        if (file.exists() && file.isFile()) {
            List<String> lines = FileUtil.readLines(file,Charset.defaultCharset());
            for (String line : lines) {
                line = StrUtil.trim(line);
                if (line.startsWith("#")) {
                    continue;
                }
                if (StrUtil.isNotEmpty(line)) {
                    insertSyncTable(line);
                }
            }
            return true;
        } else {
            return false;
        }
    }


    private void updateSyncTable(String table) throws SQLException {
        table = StrUtil.trim(table);

        String srcSql = "SELECT * FROM " + table;
        srcSql = srcSql.toLowerCase();
        try {
            List<Entity> entityList = srcSession.query(srcSql);
            int count = 0;
            for (Entity it : entityList) {
                Entity ot = Entity.create(table.toUpperCase());
                it.forEach((key,val) -> ot.set(key.toUpperCase(),val));
                insertOrUpdate(destSession,ot,"ID");
                count++;
            }
            System.out.println("UPDATE SYNC: " + table + ",num=" + count);
        } catch (Throwable e) {
            if (e.getMessage().contains("ORA-00942")) {
                throw new SQLSyntaxErrorException("ORA-00942: 表或视图不存在 " + table);
            } else {
                throw e;
            }
        }

    }

    private void insertSyncTable(String table) throws SQLException {
        table = StrUtil.trim(table);
        String descMaxSql = "SELECT MAX(\"ID\") AS MAX_ID FROM \"" + table + "\"";
        String readSql = "SELECT * FROM " + table + " WHERE ID > ? ORDER BY ID ASC LIMIT 3000";
        readSql = readSql.toLowerCase();
        descMaxSql = descMaxSql.toUpperCase();


        try {
            Long maxId = destSession.queryOne(descMaxSql).getLong("MAX_ID");
            if (null == maxId) {
                maxId = 0L;
            }
            List<Entity> entityList = srcSession.query(readSql,maxId);
            int count = 0;
            for (Entity it : entityList) {
                Entity ot = Entity.create(table.toUpperCase());
                it.forEach((key,val) -> ot.set(key.toUpperCase(),val));
                insertOrUpdate(destSession,ot,"ID");
            }
            if (count > 0) {
                System.out.println("INSERT SYNC: \"" + table + "\",num=" + count);
            }
        } catch (Throwable e) {
            if (e.getMessage().contains("ORA-00942")) {
                throw new SQLSyntaxErrorException("ORA-00942: 表或视图不存在 " + table);
            } else {
                throw e;
            }
        }
    }


    private void insertOrUpdate(Session session,Entity entity,String... keys) throws SQLException {
        System.out.println("insertOrUpdate:" + entity);
        session.insertOrUpdate(entity,keys);
    }

}
