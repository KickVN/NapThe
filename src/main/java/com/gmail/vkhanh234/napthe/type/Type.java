package com.gmail.vkhanh234.napthe.type;

import com.gmail.vkhanh234.napthe.ChatStatus;
import com.gmail.vkhanh234.napthe.MainConfig;
import com.gmail.vkhanh234.napthe.NapThe;
import com.gmail.vkhanh234.napthe.data.Card;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by XuanVinh on 8/4/2016.
 */
public abstract class Type {
    protected HashMap<String,String> status = new HashMap<>();

    public Type() {
        loadDefaultStatus();
    }

    public abstract Card send(ChatStatus c);
    public HashMap<String,String> getStatus(){
        return status;
    }
    public void loadDefaultStatus(){
         Iterator<Map.Entry<String, MainConfig.Nhamang>> ite =  NapThe.getPlugin().getMC().getNhamang().entrySet().iterator();
         while (ite.hasNext()){
             Map.Entry<String, MainConfig.Nhamang> entry = ite.next();
             if(!entry.getValue().enable) continue;
             status.put(entry.getKey(),entry.getValue().text);
         }
    }
}
