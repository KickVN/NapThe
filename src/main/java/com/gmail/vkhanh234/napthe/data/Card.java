package com.gmail.vkhanh234.napthe.data;

import com.gmail.vkhanh234.napthe.KUtils;
import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Card {
    public String seri,pin,mang,message;
    public Boolean saved,seen,remove=false,log=false;
    public Integer code,amount;
    public long timestamp;
    public String id=null;
    public Card(){
        saved=false;
        seen=false;
        timestamp = System.currentTimeMillis();
    }
    public Card(ConfigurationSection cs) {
        this.id=cs.getName();
        seri = cs.getString("seri");
        pin = cs.getString("pin");
        mang = cs.getString("mang");
        amount = cs.getInt("amount");
        seen = cs.getBoolean("seen");
        code = cs.getInt("code");
        timestamp = cs.getLong("timestamp");
        message = cs.getString("message");
        saved = true;
    }

    public Card(Card card) {
        id = card.id;
        seri = card.seri;
        pin = card.pin;
        mang = card.mang;
        message = card.message;
        saved = card.saved;
        seen = card.seen;
        code = card.code;
        amount = card.amount;
        timestamp = card.timestamp;
        remove = card.remove;
    }

    public Card(ResultSet res) throws SQLException {
        id = res.getLong("id")+"";
        seri = res.getString("seri");
        pin = res.getString("pin");
        mang = res.getString("mang");
        amount = res.getInt("amount");
        seen = res.getBoolean("seen");
        code = res.getInt("code");
        timestamp = res.getLong("timestamp");
        message = res.getString("message");
        saved = true;
    }

    public Card(HashMap<String, String> map) {
        id = map.containsKey("id")?map.get("id"):null;
        seri = map.containsKey("seri")?map.get("seri"):null;
        pin = map.containsKey("pin")?map.get("pin"):null;
        mang = map.containsKey("mang")?map.get("mang"):null;
        amount = map.containsKey("amount")?Integer.valueOf(map.get("amount")):null;
        seen = map.containsKey("seen")?Boolean.valueOf(map.get("seen")):null;
        code = map.containsKey("code")?Integer.valueOf(map.get("code")):null;
        timestamp = map.containsKey("timestamp")?Long.valueOf(map.get("timestamp")):-1;
        message = map.containsKey("message")?map.get("message"):null;
    }

    public boolean isCorrect(){
        return code==200;
    }

    public boolean isWaiting(){
        return code==201;
    }

    public boolean isWrong(){
        return !isCorrect() && !isWaiting();
    }

    public void setCode(int c){
        if(code==null || code!=c) {
            saved = false;
            seen = false;
        }
        code = c;
        String msg = NapThe.getPlugin().getMessage("response_text."+c);
        if(msg!=null) message = msg;
    }

    public void applyData(ConfigurationSection cs) {
        cs.set("seri",seri);
        cs.set("pin",pin);
        cs.set("mang",mang);
        cs.set("message",message);
        cs.set("seen",seen);
        cs.set("code",code);
        cs.set("amount",amount);
        cs.set("timestamp",timestamp);
    }

    public boolean isAbleToSave(){
        if(isCorrect() && !NapThe.getPlugin().getMC().isSaveCorrect()) return false;
        if(isWrong() && !NapThe.getPlugin().getMC().isSaveWrong()) return false;
        if(isWaiting() && !NapThe.getPlugin().getMC().isSaveWaiting()) return false;
        return true;
    }

    public String applyPlaceholder(String msg) {
        msg = msg.replace("{id}",id==null?"null":id);
        msg = msg.replace("{seri}",seri);
        msg = msg.replace("{pin}",pin);
        msg = msg.replace("{mang}",mang);
        msg = msg.replace("{message}",getMessage());
        msg = msg.replace("{code}",code+"");
        msg = msg.replace("{amount}",amount+"");
        if(msg.contains("{date}")){
            msg = msg.replace("{date}", KUtils.getDateText(timestamp));
        }
        return msg;
    }
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("id: "+id);
        builder.append(" seri: "+seri);
        builder.append(" pin: "+pin);
        builder.append(" mang: "+mang);
        builder.append(" saved: "+saved);
        builder.append(" seen: "+seen);
        builder.append(" code: "+code);
        builder.append(" timestamp: "+timestamp);
        builder.append(" amount: "+amount);
        builder.append(" errorMessage: "+message);
        builder.append(" remove: "+remove);
        return builder.toString();
    }

    public String getMessage() {
        return message==null?"":message;
    }

    public boolean isSimilar(Card base) {
        if(id!=null && !id.equals(base.id)) return false;
        if(seri!=null && !seri.equals(base.seri)) return false;
        if(pin!=null && !pin.equals(base.pin)) return false;
        if(mang!=null && !mang.equals(base.mang)) return false;
        if(seen!=null && !seen.equals(base.seen)) return false;
        if(code!=null && !code.equals(base.code)) return false;
        if(timestamp>0 && timestamp<base.timestamp) return false;
        if(amount!=null && !amount.equals(base.amount)) return false;
        if(message!=null && !message.equals(base.message)) return false;
        return true;
    }
}
