package com.gmail.vkhanh234.napthe.gui;

import com.gmail.vkhanh234.napthe.NapThe;
import com.gmail.vkhanh234.napthe.data.Card;
import com.gmail.vkhanh234.napthe.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HistoryGui {
    int page, maxPage;

    Player sender;
    PlayerData playerData;
    Inventory inventory;
    ArrayList<Card> list;
    int size = NapThe.getPlugin().getGuiConfig().getHistorySize();

    public HistoryGui(Player sender, PlayerData playerData, final int page, ArrayList<Card> list) {
        this.sender = sender;
        this.playerData = playerData;
        if (playerData == null) return;
        this.page = page;
        this.list = list;
        load();
    }

    private void load() {
        int amount = size - 9;
        maxPage = Double.valueOf(Math.ceil(list.size() * 1.0 / amount)).intValue();
        inventory = Bukkit.createInventory(null, NapThe.getPlugin().getGuiConfig().getHistorySize(),
                NapThe.getPlugin().getGuiConfig().getHistoryName().replace("{page}", page + "").replace("{total}", maxPage + ""));
        if (page < 1 || page > maxPage) return;
        int pos = 0;
        for (int i = (page - 1) * amount; (i < page * amount && i < list.size()); i++) {
            Card c = list.get(i);
            ItemStack item = NapThe.getPlugin().getGuiConfig().getHistoryCards().get(c.code).clone();
            if (item == null) {
                inventory.setItem(pos, new ItemStack(Material.AIR));
                pos++;
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) meta.setDisplayName(c.applyPlaceholder(meta.getDisplayName()));
            if (meta.hasLore()) {
                List<String> lore = new ArrayList<>();
                for (String s : meta.getLore()) {
                    lore.add(c.applyPlaceholder(s));
                }
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
            inventory.setItem(pos, item);
            pos++;
        }
        for (int i = 0; i < 9; ++i)
            inventory.setItem(size - 1 - i, NapThe.getPlugin().getGuiConfig().getHistoryFiller());
        if (page > 1) inventory.setItem(size - 9, NapThe.getPlugin().getGuiConfig().getHistoryPrev());
        if (page < maxPage) inventory.setItem(size - 1, NapThe.getPlugin().getGuiConfig().getHistoryNext());
        sender.openInventory(inventory);
    }

    public void click(ClickType clickType, int slot) {
        if (slot == size - 9 && page > 1) {
            sender.closeInventory();
            Bukkit.getScheduler().runTaskLater(NapThe.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    NapThe.getPlugin().showHistory(sender, Bukkit.getOfflinePlayer(playerData.getName()), page - 1);
                }
            }, 1);
            return;
        } else if (slot == size - 1 && page < maxPage) {
            sender.closeInventory();
            Bukkit.getScheduler().runTaskLater(NapThe.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    NapThe.getPlugin().showHistory(sender, Bukkit.getOfflinePlayer(playerData.getName()), page + 1);
                }
            }, 1);
            return;
        }
    }

    public void clear() {
        inventory.clear();
        list.clear();
    }
}

