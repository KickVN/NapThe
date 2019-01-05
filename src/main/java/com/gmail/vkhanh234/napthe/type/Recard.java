package com.gmail.vkhanh234.napthe.type;

import com.gmail.vkhanh234.napthe.ChatStatus;
import com.gmail.vkhanh234.napthe.data.Card;
import com.gmail.vkhanh234.napthe.data.mysql.MysqlUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.codec.binary.Hex;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.*;
import java.util.*;


public class Recard extends Type{
	private String merchant_id,secret_key;
	private int interval;
	private HikariDataSource ds;
	private String table;
	public Recard(ConfigurationSection config) {
		merchant_id = config.getString("merchant_id");
		secret_key = config.getString("secret_key");
		ConfigurationSection cs = config.getConfigurationSection("database");
		table = cs.getString("table");
		ds = MysqlUtils.connect(cs.getString("ip"),cs.getInt("port"),cs.getString("database"),cs.getString("user"),cs.getString("password"),false);
		checkTable();
	}

	public Card send(ChatStatus c) {
		String supplier = c.getMang();
		String seri = c.getSeri();
		String pincode = c.getPin();
		String mang = getCardType(supplier);
		if(mang==null) return null;
		Map<String, String> fields = new HashMap<String, String>();
		String data = merchant_id+mang+seri+pincode+c.getAmount();
		String signature = encode(secret_key,data);
		if(signature==null){
			Card r = c.getCard();
			r.systemError();
			return r;
		}
		String playername = c.getPlayer()==null?"error no player":c.getPlayer().getName();
		fields.put("merchant_id",merchant_id);
		fields.put("secret_key",secret_key);
		fields.put("type",mang);
		fields.put("amount", c.getAmount()+"");
		fields.put("serial", seri);
		fields.put("code", pincode);
		fields.put("comment", playername);
		fields.put("signature", signature);
		long time = System.currentTimeMillis();
		String requests = this.createRequests(fields);
		String post = post("https://recard.vn/api/card",requests);
		if(post==null || post==""){
			Card r = c.getCard();
			r.systemError();
			return r;
		}
		Response response = new Gson().fromJson(post,Response.class);
		Card r = c.cloneCard();
		r.seen = true;
		r.transaction_code = response.transaction_code;
		if(response.transaction_code!=null){
			r.setCode(203);
			addCard(playername,Integer.valueOf(mang),seri,pincode,c.getAmount(),c.getPlayer()==null?"":c.getPlayer().getAddress().getHostString(),response.transaction_code,time);
		}
		else {
			r.code = 422;
			r.message = getError(post);
		}
		return r;
	}

	private String getError(String post) {
		JsonObject object = new Gson().fromJson(post,JsonObject.class);
//		JsonElement element = jsonArray.get(0);
//		JsonObject object = element.getAsJsonObject();
		List<String> list = new ArrayList<>();
		Iterator<Map.Entry<String, JsonElement>> ite = object.entrySet().iterator();
		HashMap<String,String> newStatus = new HashMap<>();
		while (ite.hasNext()){
			Map.Entry<String, JsonElement> t = ite.next();
			list.add(t.getValue().getAsJsonArray().get(0).getAsString());
		};
		String msg ="";
		for(String s:list){
			msg+=s+" ";
		}
		return msg.substring(0,msg.length()-1);
	}

	private String post(String link,String data) {
		try {
			installAllTrustManager();
			URL url = new URL(link);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-length", String.valueOf(data.length()));
			conn.setUseCaches(false);
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			PrintWriter post = new PrintWriter(conn.getOutputStream());
			post.print(data);
			post.close();

			if (conn.getResponseCode() != 200 && conn.getResponseCode()!=422) {
				return null;
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getResponseCode()==200?conn.getInputStream():conn.getErrorStream()));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
//				System.out.println(line);
				result.append(line);
			}
			return result.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public void installAllTrustManager() {
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		}};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection
					.setDefaultHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String urlHostname,
											  SSLSession _session) {
							return true;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String encode(String key, String data){
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private String createRequests(Map<String, String> map) {
		String url_params = "";
		for (Map.Entry entry : map.entrySet()) {
			if (url_params == "")
				url_params += entry.getKey() + "=" + entry.getValue();
			else
				url_params += "&" + entry.getKey() + "=" + entry.getValue();
		}
		return url_params;
	}
	private String getCardType(String supplier) {
		switch (supplier){
			case("VIETTEL"): return "1";
			case("MOBI"): return "2";
			case("MOBIFONE"): return "2";
			case("VINA"): return "3";
			case("VINAPHONE"): return "3";
		}
		return null;
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

	}private void closeConnection(Connection connection, Statement statement, ResultSet res) {
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

	private void importDefaultTable() {
		Connection connection=null;
		Statement statement = null;
		try {
			connection = ds.getConnection();
			statement = connection.createStatement();
			statement.execute("CREATE TABLE `"+table+"` (\n" +
					"  `id` INT NOT NULL AUTO_INCREMENT,\n" +
					"  `username` VARCHAR(45) NOT NULL,\n" +
					"  `type` TINYINT(4) NOT NULL,\n" +
					"  `serial` VARCHAR(45) NOT NULL,\n" +
					"  `code` VARCHAR(45) NOT NULL,\n" +
					"  `amount` INT(11) NOT NULL DEFAULT 0,\n" +
					"  `real_amount` INT(11) NOT NULL DEFAULT 0,\n" +
					"  `status` TINYINT(4) NULL DEFAULT 0,\n" +
					"  `ip` VARCHAR(45) NOT NULL,\n" +
					"  `transaction_code` CHAR(36) NOT NULL,\n" +
					"  `created_at` DATETIME NULL,\n" +
					"  `updated_at` DATETIME NULL,\n" +
					"  PRIMARY KEY (`id`));");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection,statement);
		}
	}

	public void addCard(String username, int type, String serial, String code, int amount, String ip, String trans_code,long time) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = ds.getConnection();
			statement = connection.prepareStatement("INSERT INTO "+table+" ( `username`, `type`, `serial`, `code`, `amount`, `ip`, `transaction_code`, `created_at`)" +
							" VALUES (?,?,?,?,?,?,?,?);");
			statement.setString(1,username);
			statement.setInt(2,type);
			statement.setString(3,serial);
			statement.setString(4,code);
			statement.setInt(5,amount);
			statement.setString(6,ip);
			statement.setString(7,trans_code);
			statement.setTimestamp(8,new Timestamp(time));
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			closeConnection(connection,statement);
		}
	}

	class Response{
		int success;
		String transaction_code,message;
		List<String> errors;
	}

}
