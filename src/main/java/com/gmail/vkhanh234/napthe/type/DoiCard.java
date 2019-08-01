package com.gmail.vkhanh234.napthe.type;

import com.gmail.vkhanh234.napthe.ChatStatus;
import com.gmail.vkhanh234.napthe.data.Card;
import com.gmail.vkhanh234.napthe.utils.RequestUtils;
import com.google.gson.Gson;
import org.bukkit.configuration.ConfigurationSection;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class DoiCard extends Type {
    private static final String CHARING_COMMAND = "charging";
    private String partner_id, partner_key;
    private int interval;

    public DoiCard(ConfigurationSection config) {
        partner_id = config.getString("partner_id");
        partner_key = config.getString("partner_key");
    }

    public String generateId() {
        return String.valueOf(new Random().nextInt(100))
                + String.valueOf(System.currentTimeMillis() / 1000).substring(3);
    }

    public Card send(ChatStatus c) {
        String supplier = c.getMang();
        boolean quickMode = supplier.equalsIgnoreCase("GATE");
        String url = quickMode ? "http://doicard.vn/api/charging" : "http://webthefull.com/chargingws";
        String seri = c.getSeri();
        String pincode = c.getPin();
        String telco = getCardType(supplier);
        int amount = c.getAmount();
        String requestId = generateId();
        String concat = quickMode ? partner_key + pincode + CHARING_COMMAND + partner_id + requestId + seri + telco :
                partner_id + partner_key + telco + pincode + seri + amount + requestId;
        String signature = md5(concat);
        if (signature == null) {
            Card r = c.getCard();
            r.systemError();
            return r;
        }
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("partner_id", partner_id);
        fields.put("request_id", requestId);
        fields.put("telco", telco);
        fields.put("serial", seri);
        fields.put("code", pincode);
        fields.put("sign", signature);
        if (quickMode) fields.put("command", CHARING_COMMAND);
        else fields.put("amount", c.getAmount() + "");
        String requests = RequestUtils.createRequests(fields);
        String post = RequestUtils.post(url, requests);
        if (post == null || post.isEmpty()) {
            Card r = c.getCard();
            r.systemError();
            return r;
        }
        Response response = new Gson().fromJson(post, Response.class);
        Card r = c.cloneCard();
        r.seen = true;
        r.transaction_code = response.trans_id;
        if (response.status == 1) {
            r.setCode(200);
        } else if (response.status == 99) {
            r.setCode(203);
        } else {
            r.code = 202;
            r.message = response.message;
        }
        return r;
    }

    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] arr = md.digest(data.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < arr.length; ++i) {
                sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String getCardType(String supplier) {
        switch (supplier) {
            case ("MOBI"):
                return "MOBIFONE";
            case ("VINA"):
                return "VINAPHONE";
        }
        return supplier;
    }

    class Response {
        int status;
        String trans_id, message;
    }

}
