package com.gmail.vkhanh234.napthe.type;

import com.gmail.vkhanh234.napthe.ChatStatus;
import com.gmail.vkhanh234.napthe.NapThe;
import com.gmail.vkhanh234.napthe.data.Card;
import com.gmail.vkhanh234.napthe.utils.RequestUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


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
				loadTelco();
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
		String requests = RequestUtils.createRequests(fields);
		String get = RequestUtils.get("https://sv.gamebank.vn/api/card2?" + requests);
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

	public void loadTelco() {
		String get = RequestUtils.get("https://sv.gamebank.vn/trang-thai-he-thong-2");
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

	private String getCardType(String supplier) {
		switch (supplier){
			case("VIETTEL"): return "1";
			case("MOBI"): return "2";
			case("MOBIPHONE"): return "2";
			case("MOBIFONE"): return "2";
			case("VINA"): return "3";
			case("VINAPHONE"): return "3";
			case("GATE"): return "4";
			case("VIETNAMMOBILE"): return "6";
			case("VNM"): return "6";
			case("Zing"): return "7";
		}
		return null;
	}

	class Response{
		int code,info_card;
		String msg;
	}

}
