package com.gmail.vkhanh234.napthe.data;

import org.bukkit.OfflinePlayer;

import java.util.List;

public interface Data {
    public boolean isDetailAvailable();
    public List<TopEntry> getTop(long time);
    public PlayerData loadPlayer(OfflinePlayer p);
    public boolean addCard(PlayerData p, Card c);
    public void savePlayer(PlayerData data);
    public int getTotalAmount(long time);
    public void close();
    public Card getCard(String id);
    public boolean updateCard(Card c);
    public boolean updateCard(PlayerData data,Card c);
    public List<Card> getCard(Card base);
}
