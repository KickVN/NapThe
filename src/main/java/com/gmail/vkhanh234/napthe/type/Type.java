package com.gmail.vkhanh234.napthe.type;

import com.gmail.vkhanh234.napthe.ChatStatus;
import com.gmail.vkhanh234.napthe.NapThe;
import com.gmail.vkhanh234.napthe.data.Card;

import java.util.HashMap;

/**
 * Created by XuanVinh on 8/4/2016.
 */
public abstract class Type {
    protected HashMap<String,String> status = NapThe.getPlugin().getMC().getNhamang();
    public abstract Card send(ChatStatus c);
    public HashMap<String,String> getStatus(){
        return status;
    }
}
