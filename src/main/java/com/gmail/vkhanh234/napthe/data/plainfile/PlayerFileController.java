package com.gmail.vkhanh234.napthe.data.plainfile;

import com.gmail.vkhanh234.napthe.NapThe;
import com.gmail.vkhanh234.napthe.data.Card;
import com.gmail.vkhanh234.napthe.data.PlayerData;
import com.gmail.vkhanh234.napthe.data.TopEntry;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by KickVN on 15/2/2016.
 */
public class PlayerFileController {
    public static final String FOLDER_NAME = "players";
    public static final File FOLDER = new File(NapThe.getPlugin().getDataFolder(),FOLDER_NAME);

    private int totalChance=0;
    public PlayerFileController(){
        if(!FOLDER.exists()) {
            FOLDER.mkdir();
        }
    }

    public List<TopEntry> getTop(long time){
        List<TopEntry> top = new ArrayList<>();
        for(File file:FOLDER.listFiles()){
            if(!file.getName().endsWith(".yml")) continue;
            PlayerData data = loadFileData(file);
            if(data==null || data.countCards()==0) continue;
            int amount = data.getSumAmount(time);
            if(amount==0) continue;
            top.add(new TopEntry(data.getName(),amount));
        }
        return top;
    }

    private PlayerData loadFileData(File file) {
        return new PlayerData(file);
    }

    public PlayerData loadPlayerData(OfflinePlayer p) {
        File file = new File(FOLDER,p.getUniqueId().toString()+".yml");
        if(!file.exists()) return new PlayerData(p);
        return loadFileData(file);
    }


    public void savePlayer(PlayerData data) {
        try {
            File file = new File(FOLDER, data.getUniqueId() + ".yml");
            if (!file.exists()) file.createNewFile();
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("name",data.getName());
            HashMap<String, Card> cards = data.getCards();
            for (Card c : cards.values()) {
                if (c.saved) continue;
                String id = "cards."+c.id;
                if(c.remove){
                    config.set(id,null);
                }
                else {
                    config.createSection(id);
                    ConfigurationSection cs = config.getConfigurationSection(id);
                    c.applyData(cs);
                    config.set(id, cs);
                }
                c.saved=true;
            }
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTotalAmount(long time) {
        int t=0;
        for(File file:FOLDER.listFiles()){
            if(!file.getName().endsWith(".yml")) continue;
            PlayerData data = loadFileData(file);
            if(data==null || data.countCards()==0) continue;
            int amount = data.getSumAmount(time);
            if(amount==0) continue;
            t+=amount;
        }
        return t;
    }

    public List<Card> getCard(Card base) {
        List<Card> result = new ArrayList<>();
        for(File file:FOLDER.listFiles()){
            if(!file.getName().endsWith(".yml")) continue;
            PlayerData data = loadFileData(file);
            if(data==null || data.countCards()==0) continue;
            for(Card c:data.getCards().values()){
                if(base.isSimilar(c)) result.add(c);
            }
        }
        return result;
    }
}
