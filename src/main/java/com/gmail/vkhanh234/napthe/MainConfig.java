package com.gmail.vkhanh234.napthe;

import com.gmail.vkhanh234.napthe.data.Card;
import com.gmail.vkhanh234.napthe.data.mysql.MysqlData;
import com.gmail.vkhanh234.napthe.data.plainfile.PlainfileData;
import com.gmail.vkhanh234.napthe.type.GameBank;
import com.gmail.vkhanh234.napthe.type.Manual;
import com.gmail.vkhanh234.napthe.type.Recard;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by XuanVinh on 9/19/2016.
 */
public class MainConfig {
    private FileConfiguration config;
    private HashMap<String,String> msg = new HashMap<>();
    File configFile;
    String filename = "config.yml";

    HashMap<String,List<String>> prizes = new HashMap<>();
    LinkedHashMap<String,Nhamang> nhamang = new LinkedHashMap<>();
    String mode;
    boolean priceEnable;
    LinkedHashMap<String,String> prices = new LinkedHashMap<>();
    boolean saveCorrect, saveWrong, saveWaiting;
    int rowPerPage;
    String inputRegex,cancelText;


    public MainConfig(){
        init();
    }

    void load() {
        loadMang();
        loadPrices();
        loadPrizes();
        loadMessages();
        loadData();
        loadMode();

        rowPerPage = config.getInt("Row_Per_page");
        inputRegex = config.getString("Input_Regex");
        cancelText= config.getString("Cancel_Text");
    }

    public void loadData() {
        saveCorrect = config.getBoolean("Data.save.correct_card");
        saveWrong = config.getBoolean("Data.save.wrong_card");
        saveWaiting = config.getBoolean("Data.save.waiting_card");
        String type = config.getString("Data.type");
        try {
            switch (type) {
                case ("plainfile"):
                    NapThe.getPlugin().setData(new PlainfileData(config.getConfigurationSection("Data.plainfile")));
                    break;
                case ("mysql"):
                    NapThe.getPlugin().setData(new MysqlData(config.getConfigurationSection("Data.mysql")));
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Can't load Data! Disabling...");
            Bukkit.getPluginManager().disablePlugin(NapThe.getPlugin());
        }
    }

    private void loadMode() {
        mode = config.getString("Mode");
        if(mode.equalsIgnoreCase("GameBank")) NapThe.getPlugin().setType(new GameBank(config.getConfigurationSection("GameBank")));
        else if(mode.equalsIgnoreCase("Manual")) NapThe.getPlugin().setType(new Manual(config.getConfigurationSection("Manual")));
        else if(mode.equalsIgnoreCase("Recard")) NapThe.getPlugin().setType(new Recard(config.getConfigurationSection("Recard")));
    }

    private void loadPrices() {
        priceEnable = config.getBoolean("ChoosePrice.enable");
        if(!priceEnable) return;
        ConfigurationSection cs = config.getConfigurationSection("ChoosePrice.values");
        for(String k:cs.getKeys(false)){
            if(!cs.getBoolean(k+".enable")) continue;
            prices.put(k,KUtils.convertColor(cs.getString(k+".text")));
        }

    }

    private void loadMessages() {
        ConfigurationSection cs = config.getConfigurationSection("Message");
        for(String k:cs.getKeys(true)){
            msg.put(k, KUtils.convertColor(cs.getString(k)));
        }
    }

    private void loadMang() {
        ConfigurationSection cs = config.getConfigurationSection("Mang");
        for(String k:cs.getKeys(false)){
//            if(!cs.getBoolean(k+".enable")) continue;
            nhamang.put(k.toUpperCase(),new Nhamang(cs.getBoolean(k+".enable"),KUtils.convertColor(cs.getString(k+".text"))));
        }
    }

    private void loadPrizes() {
        ConfigurationSection cs = config.getConfigurationSection("Prize");
        for(String k:cs.getKeys(false)) prizes.put(k,cs.getStringList(k));
    }

    private void init() {
        configFile = new File(NapThe.getPlugin().getDataFolder(), filename);
        try {
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() throws IOException, InvalidConfigurationException {
        if(!configFile.exists()) {
            NapThe.getPlugin().saveResource(filename, true);
            config = YamlConfiguration.loadConfiguration(configFile);
        }
        else{
            config = YamlConfiguration.loadConfiguration(configFile);
            FileConfiguration c = YamlConfiguration.loadConfiguration(new InputStreamReader(NapThe.getPlugin().getResource(filename)));
            for(String k:c.getKeys(true)){
                if(!config.contains(k)){
                    config.set(k,c.get(k));
                }
            }
            config.save(configFile);
        }
    }

    private List<String> getStringList(String s) {
        List<String> t = new ArrayList<>();
        for(String str:config.getStringList(s)){
            t.add(KUtils.convertColor(str));
        }
        return t;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getString(String s) {
        return KUtils.convertColor(config.getString(s));
    }

    public String getMessage(String s){
        return msg.get(s);
    }

    public HashMap<String, String> getPrices() {
        return prices;
    }

    public boolean isPriceEnable() {
        return priceEnable;
    }

    public List<String> getPrize(String s) {
        return prizes.get(s);
    }

    public HashMap<String, Nhamang> getNhamang() {
        return nhamang;
    }

    public boolean isSaveCorrect() {
        return saveCorrect;
    }

    public boolean isSaveWrong() {
        return saveWrong;
    }

    public boolean isSaveWaiting() {
        return saveWaiting;
    }

    public String getResponse(Card r) {
        String msg = getMessage("response_message."+r.code);
        if(msg==null) msg = getMessage("response_message.default");
        msg = r.applyPlaceholder(msg);
        return msg;
    }

    public int getRowPerPage() {
        return rowPerPage;
    }

    public String getInputRegex() {
        return inputRegex;
    }

    public String getCancelText() {
        return cancelText;
    }

    public class Nhamang{
        public boolean enable;
        public String text;

        public Nhamang(boolean enable, String text) {
            this.enable = enable;
            this.text = text;
        }
    }
}
