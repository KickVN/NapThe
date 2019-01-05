package com.gmail.vkhanh234.napthe.data.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MysqlUtils {
    public static HikariDataSource connect(String ip,int port,String database,String user,String password,boolean cache){
        String url = "jdbc:mysql://"+ip+":"+port+"/"+database+"?useUnicode=true&characterEncoding=utf-8";
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", cache);
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }
}
