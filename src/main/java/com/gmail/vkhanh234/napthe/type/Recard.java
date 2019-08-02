package com.gmail.vkhanh234.napthe.type;

import com.gmail.vkhanh234.napthe.ChatStatus;
import com.gmail.vkhanh234.napthe.data.Card;
import com.gmail.vkhanh234.napthe.utils.RequestUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Hex;
import org.bukkit.configuration.ConfigurationSection;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;


public class Recard extends Type{
	private String merchant_id,secret_key;
	private int interval;
	public Recard(ConfigurationSection config) {
		merchant_id = config.getString("merchant_id");
		secret_key = config.getString("secret_key");
	}

	public Card send(ChatStatus c) {
		String supplier = c.getMang();
		String seri = c.getSeri();
		String pincode = c.getPin();
		String mang = getCardType(supplier);
		if(mang==null) return null;
		String data = merchant_id+mang+seri+pincode+c.getAmount();
		String signature = sha(secret_key, data);
		if(signature==null){
			Card r = c.getCard();
			r.systemError();
			return r;
		}
		String playername = c.getPlayer()==null?"error no player":c.getPlayer().getName();
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("merchant_id",merchant_id);
		fields.put("secret_key",secret_key);
		fields.put("type",mang);
		fields.put("amount", c.getAmount()+"");
		fields.put("serial", seri);
		fields.put("code", pincode);
		fields.put("comment", playername);
		fields.put("signature", signature);
		String requests = RequestUtils.createRequests(fields);
		String post = RequestUtils.post("https://recard.vn/api/card", requests);
		if (post == null || post.isEmpty()) {
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
		}
		else {
			r.code = 202;
			r.message = getError(post);
		}
		return r;
	}

	private String getError(String post) {
		JsonObject object = new Gson().fromJson(post,JsonObject.class);
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

	public static String sha(String key, String data) {
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

	class Response{
		int success;
		String transaction_code,message;
		List<String> errors;
	}

}
