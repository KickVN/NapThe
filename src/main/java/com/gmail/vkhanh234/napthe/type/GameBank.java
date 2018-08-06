package com.gmail.vkhanh234.napthe.type;

import com.gmail.vkhanh234.napthe.ChatStatus;
import com.gmail.vkhanh234.napthe.NapThe;
import com.gmail.vkhanh234.napthe.data.Card;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class GameBank extends Type{
	private String merchant_id,api_user,api_password;
	private int interval;

	public GameBank(ConfigurationSection config) {
		merchant_id = config.getString("merchant_id");
		api_user = config.getString("api_user");
		api_password = config.getString("api_password");
		interval = config.getInt("status_check_interval");

		Bukkit.getScheduler().runTaskTimerAsynchronously(NapThe.getPlugin(), new Runnable() {
			@Override
			public void run() {
				loadMang();
			}
		},1,interval);
	}

	public Card send(ChatStatus c) {
		String supplier = c.getMang();
		String seri = c.getSeri();
		String pincode = c.getPin();
		String mang = getCardType(supplier);
		if(mang==null) return null;
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("merchant_id",merchant_id);
		fields.put("api_user",api_user);
		fields.put("api_password", api_password);
		fields.put("card_type",mang);
		fields.put("price_guest", c.getAmount()+"");
		fields.put("seri", seri);
		fields.put("pin", pincode);
		fields.put("note", c.getPlayer()==null?"error no player":c.getPlayer().getName());
		String requests = this.createRequests(fields);
		String get = get("http://sv.gamebank.vn/api/card2?"+requests);
		if(get==null || get=="") return null;
		Response response = new Gson().fromJson(get,Response.class);
		Card r = c.cloneCard();
		r.seen = true;
		r.message = response.msg;
		if (response.code == 0) {
			r.setCode(200);
			r.amount = response.info_card;
		}
		else {
			r.code = response.code;
		}
		return r;
	}

	public void loadMang() {
		String get = get("https://sv.gamebank.vn/trang-thai-he-thong-2");
		if(get==null || get=="") return;
		JsonArray jsonArray = new Gson().fromJson(get,JsonArray.class);
		JsonElement element = jsonArray.get(0);
		JsonObject object = element.getAsJsonObject();
		Iterator<Map.Entry<String, JsonElement>> ite = object.entrySet().iterator();
		HashMap<String,String> newStatus = new HashMap<>();
		while (ite.hasNext()){
			Map.Entry<String, JsonElement> t = ite.next();
			String key = t.getKey().toUpperCase();
			if(status.containsKey(key)){
				int c = t.getValue().getAsInt();
				if(c==1) newStatus.put(key,status.get(key));
			}
		}
		status = newStatus;
	}

	private String get(String link) {
		try {
			installAllTrustManager();
			URL url = new URL(link);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setUseCaches(false);
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			if (conn.getResponseCode() != 200) {
				return null;
			}
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
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
			case("VINA"): return "3";
			case("GATE"): return "4";
		}
		return null;
	}

	class Response{
		int code,info_card;
		String msg;
	}

}
