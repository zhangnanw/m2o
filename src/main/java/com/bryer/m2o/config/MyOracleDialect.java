package com.bryer.m2o.config;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.dialect.impl.OracleDialect;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.SqlLog;
import cn.hutool.db.sql.SqlUtil;
import cn.hutool.db.sql.Wrapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;

/**
 * @author zhangnan@yansou.org
 */
public class MyOracleDialect extends OracleDialect {

    public MyOracleDialect() {
        super();
        //Oracle所有字段名用双引号包围，防止字段名或表名与系统关键字冲突
        wrapper = new Wrapper('"');
    }

    @Override
    public PreparedStatement psForInsert(Connection conn,Entity entity) throws SQLException {
        final SqlBuilder insert = SqlBuilder.create(wrapper).insert(entity,this.dialectName());


        return prepareStatement(conn,insert);
    }

    private static PreparedStatement prepareStatement(Connection conn,SqlBuilder sqlBuilder) throws SQLException {
        return prepareStatement(conn,sqlBuilder.build(),sqlBuilder.getParamValueArray());
    }

    private static PreparedStatement prepareStatement(Connection conn,String sql,Object... params) throws SQLException {
        Assert.notBlank(sql,"Sql String must be not blank!");
        sql = sql.trim();
        SqlLog.INSTASNCE.log(sql,params);
        PreparedStatement ps = conn.prepareStatement(sql);
        return fillParams(ps,params);
    }

    private static PreparedStatement fillParams(PreparedStatement ps,Object... params) throws SQLException {
        if (ArrayUtil.isEmpty(params)) {
            // 无参数
            return ps;
        }
        Object param;
        for (int i = 0; i < params.length; i++) {
            int paramIndex = i + 1;
            param = params[i];
            if (null != param) {
                if (param instanceof java.util.Date) {
                    // 日期特殊处理
                    if (param instanceof java.sql.Date) {
                        ps.setDate(paramIndex,(java.sql.Date) param);
                    } else if (param instanceof java.sql.Time) {
                        ps.setTime(paramIndex,(java.sql.Time) param);
                    } else {
                        ps.setTimestamp(paramIndex,SqlUtil.toSqlTimestamp((java.util.Date) param));
                    }
                } else if (param instanceof Number) {
                    // 针对大数字类型的特殊处理
                    if (param instanceof BigInteger) {
                        // BigInteger转为Long
                        ps.setLong(paramIndex,((BigInteger) param).longValue());
                    } else if (param instanceof BigDecimal) {
                        // BigDecimal的转换交给JDBC驱动处理
                        ps.setBigDecimal(paramIndex,(BigDecimal) param);
                    } else {
                        // 普通数字类型按照默认传入
                        ps.setObject(paramIndex,param);
                    }
                } else {
                    ps.setObject(paramIndex,param);
                }
            } else {
                final ParameterMetaData pmd = ps.getParameterMetaData();
                int sqlType = Types.VARCHAR;
                try {
                    sqlType = pmd.getParameterType(paramIndex);
                } catch (SQLException e) {
                    // ignore
                    // log.warn("Null param of index [{}] type get failed, by: {}", paramIndex, e.getMessage());
                }
                ps.setNull(paramIndex,sqlType);
            }
        }
        return ps;
    }

}
