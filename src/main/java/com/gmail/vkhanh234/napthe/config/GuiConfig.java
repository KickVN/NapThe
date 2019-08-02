package com.gmail.vkhanh234.napthe.config;

import com.gmail.vkhanh234.napthe.NapThe;
import com.gmail.vkhanh234.napthe.utils.KUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by KickVN on 15/2/2016.
 */
public class GuiConfig {
    private FileConfiguration config;
    private File configFile;
    private String filename = "gui.yml";

    private String historyName;
    private int historySize;
    private ItemStack historyNext, historyPrev, historyFiller;
    private HashMap<String, ItemStack> historyCards = new HashMap<>();

    public GuiConfig() {
        configFile = new File(NapThe.getPlugin().getDataFolder(), filename);
        try {
            update();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        load();
    }

    private void load() {
        historySize = config.getInt("History.size");
        historyName = config.getString("History.name");
        historyNext = KUtils.getItem(config.getConfigurationSection("History.items.next"));
        historyPrev = KUtils.getItem(config.getConfigurationSection("History.items.prev"));
        historyFiller = KUtils.getItem(config.getConfigurationSection("History.items.filler"));
        ConfigurationSection cs = config.getConfigurationSection("History.items.cards");
        for (String s : cs.getKeys(false)) {
            historyCards.put(s, KUtils.getItem(cs.getConfigurationSection(s)));
        }
    }

    public void update() throws IOException, InvalidConfigurationException {
        if (!configFile.exists()) {
            configFile.createNewFile();
            NapThe.getPlugin().saveResource(filename, true);
            config = YamlConfiguration.loadConfiguration(configFile);
        } else {
            config = YamlConfiguration.loadConfiguration(configFile);
            try {
                FileConfiguration c = YamlConfiguration.loadConfiguration(new InputStreamReader(NapThe.getPlugin().getResource(filename)));
                for (String k : c.getKeys(true)) {
                    if (!config.contains(k)) {
                        config.set(k, c.get(k));
                    }
                }
                config.save(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration get() {
        return config;
    }

    public ConfigurationSection getSection(String s) {
        if (!config.contains(s)) return null;
        return config.getConfigurationSection(s);
    }

    public int getHistorySize() {
        return historySize;
    }

    public ItemStack getHistoryNext() {
        return historyNext.clone();
    }

    public ItemStack getHistoryPrev() {
        return historyPrev.clone();
    }

    public ItemStack getHistoryFiller() {
        return historyFiller.clone();
    }

    public HashMap<String, ItemStack> getHistoryCards() {
        return historyCards;
    }

    public String getHistoryName() {
        return historyName;
    }

    public ItemStack fetchHistoryItem(int code) {
        String key = String.valueOf(code);
        ItemStack item = historyCards.containsKey(key) ? historyCards.get(key) : historyCards.get("default");
        return item.clone();
    }
}
