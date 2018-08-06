package com.gmail.vkhanh234.napthe.data.mysql;

import com.gmail.vkhanh234.napthe.data.Card;
import com.gmail.vkhanh234.napthe.data.Data;
import com.gmail.vkhanh234.napthe.data.PlayerData;
import com.gmail.vkhanh234.napthe.data.TopEntry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by KickVN on 4/20/2017.
 */
public class MysqlData implements Data {
    HikariDataSource ds;
    String table;
    public MysqlData(ConfigurationSection cs) throws SQLException {
        String url = "jdbc:mysql://"+cs.getString("ip")+":"+cs.getInt("port")+"/"+cs.getString("database")+"?useUnicode=true&characterEncoding=utf-8";
//        connection= DriverManager.getConnection(url,cs.getString("user"),cs.getString("password"));
        table = cs.getString("table_prefix")+"napthe";
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(cs.getString("user"));
        config.setPassword(cs.getString("password"));
        config.addDataSourceProperty("cachePrepStmts", cs.getBoolean("cache"));
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);
        checkTable();
    }

    private void checkTable() {
        Connection connection=null;
        Statement statement = null;
        try {
            connection = ds.getConnection();
            statement = connection.createStatement();
            statement.executeQuery("SELECT 1 FROM "+table+" LIMIT 1;");
        } catch (SQLException e) {
            Bukkit.getLogger().info("Table is not exist. Creating default table.");
            importDefaultTable();
        } finally {
            closeConnection(connection,statement);
        }
    }

    private void importDefaultTable() {
        Connection connection=null;
        Statement statement = null;
        try {
            connection = ds.getConnection();
            statement = connection.createStatement();
            statement.execute("CREATE TABLE `"+table+"` (\n" +
                    "  `id` bigint(20) NOT NULL,\n" +
                    "  `uuid` text NOT NULL,\n" +
                    "  `playername` text NOT NULL,\n" +
                    "  `seri` text NOT NULL,\n" +
                    "  `pin` text NOT NULL,\n" +
                    "  `mang` text NOT NULL,\n" +
                    "  `amount` int(11) NOT NULL,\n" +
                    "  `code` int(11) NOT NULL,\n" +
                    "  `seen` tinyint(1) NOT NULL,\n" +
                    "  `timestamp` bigint(20) NOT NULL,\n" +
                    "  `message` text NOT NULL\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_vietnamese_ci;");
            statement.execute("ALTER TABLE `"+table+"`\n" +
                    "  ADD PRIMARY KEY (`id`);");
            statement.execute("ALTER TABLE `"+table+"`\n" +
                    "  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection,statement);
        }
    }

    @Override
    public boolean isDetailAvailable() {
        return true;
    }

    @Override
    public List<TopEntry> getTop(long time) {
        HashMap<String,Integer> map = new HashMap<>();
        List<TopEntry> top = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet res = null;
        try {
            connection = ds.getConnection();
            statement = connection.createStatement();
            res = statement.executeQuery("SELECT playername,amount FROM "+table+" WHERE `timestamp`>="+(System.currentTimeMillis()-time)+" AND code=200;");
            while (res.next()) {
                map.put(res.getString("playername"),res.getInt("amount"));
            }
            for(String k:map.keySet()) top.add(new TopEntry(k,map.get(k)));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection,statement,res);
        }
        return top;
    }

    @Override
    public PlayerData loadPlayer(OfflinePlayer p) {
        PlayerData data = new PlayerData(p);
        Connection connection = null;
        Statement statement = null;
        ResultSet res = null;
        try {
            connection = ds.getConnection();
            statement = connection.createStatement();
            res = statement.executeQuery("SELECT * FROM "+table+" WHERE uuid = '"+p.getUniqueId()+"';");
            while (res.next()) {
                data.addCard(new Card(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection,statement,res);
        }
        return data;
    }

    @Override
    public boolean addCard(PlayerData p, Card c) {
        if(c.id!=null) {
            return updateCard(c);
        }
        Connection connection = null;
        PreparedStatement statement = null;
        boolean b=false;
        try {
            connection = ds.getConnection();
            statement = connection.prepareStatement("INSERT INTO "+table+" ( `uuid`, `playername`, `seri`, `pin`, `mang`, `amount`, `seen`, `code`, `timestamp`, `message`)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?);",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,p.getUniqueId());
            statement.setString(2,p.getName());
            statement.setString(3,c.seri);
            statement.setString(4,c.pin);
            statement.setString(5,c.mang);
            statement.setInt(6,c.amount);
            statement.setBoolean(7,c.seen);
            statement.setInt(8,c.code);
            statement.setLong(9,c.timestamp);
            statement.setString(10,c.message);
            statement.executeUpdate();
            c.saved=true;
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                c.id = generatedKeys.getLong(1)+"";
            }
            b=true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnection(connection,statement);
        }
        return b;
    }

    public boolean updateCard(Card c) {
        if(c.id==null) return false;
        if(c.remove){
            return removeCard(c);
        }
        Connection connection = null;
        PreparedStatement statement = null;
        boolean b=false;
        try {
            connection = ds.getConnection();
            statement = connection.prepareStatement("UPDATE "+table+" SET seen = ?, code = ?, message = ? WHERE id = ?;");
            statement.setBoolean(1,c.seen);
            statement.setInt(2,c.code);
            statement.setString(3,c.getMessage());
            statement.setLong(4,Long.valueOf(c.id));
            statement.executeUpdate();
            c.saved=true;
            b = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnection(connection,statement);
        }
        return b;
    }

    private boolean removeCard(Card c) {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean b=false;
        try {
            connection = ds.getConnection();
            statement = connection.prepareStatement("DELETE FROM "+table+" WHERE id = ?;");
            statement.setLong(1,Long.valueOf(c.id));
            statement.executeUpdate();
            c.saved=true;
            b = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnection(connection,statement);
        }
        return b;
    }

    public boolean updateCard(PlayerData data, Card c) {
        return updateCard(c);
    }

    @Override
    public void savePlayer(PlayerData data) {
        for(Card c:data.getCards().values()){
            if(c.saved) continue;
            addCard(data,c);
        }
    }

    @Override
    public int getTotalAmount(long time) {
        Connection connection = null;
        Statement statement = null;
        ResultSet res = null;
        int total=0;
        try {
            connection = ds.getConnection();
            statement = connection.createStatement();
            res = statement.executeQuery("SELECT SUM(amount) AS total FROM "+table+" WHERE `timestamp`>="+(System.currentTimeMillis()-time)+" AND code=200;");
            while (res.next()) {
                total = res.getInt("total");
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection,statement,res);
        }
        return total;
    }

    @Override
    public void close() {
        ds.close();
    }

    @Override
    public Card getCard(String id) {
        if(!id.matches("\\d+")) return null;
        Connection connection = null;
        Statement statement = null;
        ResultSet res = null;
        Card card = null;
        try {
            connection = ds.getConnection();
            statement = connection.createStatement();
            res = statement.executeQuery("SELECT * FROM "+table+" WHERE id = "+Long.valueOf(id)+";");
            while (res.next()) {
                card = new Card(res);
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection,statement,res);
        }
        return card;
    }

    @Override
    public List<Card> getCard(Card base) {
        String where = getCardWhereClause(base);
        List<Card> result = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet res = null;
        try {
            connection = ds.getConnection();
            statement = connection.createStatement();
            res = statement.executeQuery("SELECT * FROM "+table+" WHERE "+where+";");
            while (res.next()) {
                result.add(new Card(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection,statement,res);
        }
        return result;
    }

    private String getCardWhereClause(Card base) {
        StringBuilder builder = new StringBuilder();
        if(base.id!=null) builder.append("`id`='"+base.id+"' AND ");
        if(base.seri!=null) builder.append("`seri`='"+base.seri+"' AND ");
        if(base.pin!=null) builder.append("`pin`='"+base.pin+"' AND ");
        if(base.mang!=null) builder.append("`mang`='"+base.mang+"' AND ");
        if(base.amount!=null) builder.append("`amount`='"+base.amount+"' AND ");
        if(base.seen!=null) builder.append("`seen`='"+base.seen+"' AND ");
        if(base.code!=null) builder.append("`code`='"+base.code+"' AND ");
        if(base.timestamp>0) builder.append("`timestamp`<='"+base.timestamp+"' AND ");
        if(base.message!=null) builder.append("`message`='"+base.message+"' AND ");
        String res = builder.toString();
        return res.substring(0,res.lastIndexOf(" AND "));
    }

    private void closeConnection(Connection connection, Statement statement, ResultSet res) {
        try {
            if(statement!=null) statement.close();
            if(connection!=null) connection.close();
            if(res!=null) res.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection(Connection connection, Statement statement) {
        closeConnection(connection,statement,null);
    }

}
