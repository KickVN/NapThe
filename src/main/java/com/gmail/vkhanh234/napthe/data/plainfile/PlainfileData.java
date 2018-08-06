package com.gmail.vkhanh234.napthe.data.plainfile;

import com.gmail.vkhanh234.napthe.NapThe;
import com.gmail.vkhanh234.napthe.data.Card;
import com.gmail.vkhanh234.napthe.data.Data;
import com.gmail.vkhanh234.napthe.data.PlayerData;
import com.gmail.vkhanh234.napthe.data.TopEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlainfileData implements Data {
    LogFile correctCard=null,wrongCard=null,waitingCard=null;
    PlayerFileController pfc =null;
    String correctText,wrongText,waitingText;
    public PlainfileData(ConfigurationSection cs){
        if(cs.getBoolean("quick_logs")) {
            if (NapThe.getPlugin().getMC().isSaveCorrect()) correctCard = new LogFile("correct_card");
            if (NapThe.getPlugin().getMC().isSaveWrong()) wrongCard = new LogFile("wrong_card");
            if (NapThe.getPlugin().getMC().isSaveWaiting()) waitingCard = new LogFile("waiting_card");
            correctText = cs.getString("log_text.correct");
            wrongText = cs.getString("log_text.wrong");
            waitingText = cs.getString("log_text.waiting");
        }
        if(cs.getBoolean("player_data")) pfc = new PlayerFileController();
    }

    public PlayerData loadPlayer(OfflinePlayer p){
        if(pfc==null) return null;
        return pfc.loadPlayerData(p);
    }
    public boolean isDetailAvailable(){
        return pfc!=null;
    }

    public List<TopEntry> getTop(long time){
        if(pfc==null) return null;
        return pfc.getTop(time);
    }

    public boolean addCard(PlayerData p, Card c){
        if(pfc==null) return false;
        c.id=p.getName()+"_"+c.timestamp;
        log(p,c);
        return true;
    }

    private void log(PlayerData p, Card c) {
        if(c.log) return;
        if(c.isCorrect()) correctCard.log(c.applyPlaceholder(correctText.replace("{player}",p.getName())));
        else if(c.isWrong()) wrongCard.log(c.applyPlaceholder(wrongText.replace("{player}",p.getName())));
        else if(c.isWaiting()) waitingCard.log(c.applyPlaceholder(waitingText.replace("{player}",p.getName())));
        c.log=true;
    }

    public void savePlayer(PlayerData data){
        if(pfc==null) return;
        pfc.savePlayer(data);
    }

    public int getTotalAmount(long time) {
        if(pfc==null) return -1;
        return pfc.getTotalAmount(time);
    }

    @Override
    public void close() {
    }

    @Override
    public Card getCard(String c) {
        if(pfc==null) return null;
        String[] spl = c.split("_");
        if(spl.length<2) return null;
        String name = spl[0];
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if(player==null) return null;
        PlayerData data = pfc.loadPlayerData(player);
        if(!data.getCards().containsKey(c)) return null;
        return data.getCards().get(c);
    }

    @Override
    public boolean updateCard(Card c) {
        if(pfc==null) return false;
        String[] spl = c.id.split("_");
        if(spl.length<2) return false;
        String name = spl[0];
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if(player==null) return false;
        PlayerData data = pfc.loadPlayerData(player);
        return updateCard(data,c);
    }

    public boolean updateCard(PlayerData data, Card c) {
        data.getCards().remove(c.id);
        data.addCard(c);
        pfc.savePlayer(data);
        if(c.isAbleToSave()) log(data,c);
        return true;
    }

    @Override
    public List<Card> getCard(Card base) {
        if(pfc==null) return new ArrayList<>();
        if(base.id!=null) return Collections.singletonList(getCard(base.id));
        return pfc.getCard(base);
    }

}
