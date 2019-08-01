package com.gmail.vkhanh234.napthe;

import com.gmail.vkhanh234.napthe.data.Card;
import org.bukkit.entity.Player;

/**
 * Created by Admin on 10/10/2015.
 */
public class ChatStatus {
    Stage stage = Stage.NONE;
    Player player;
    Card card = new Card();

    public void setSeri(String s) {
        card.seri=s;
    }

    public void setPin(String s) {
        card.pin=s;
    }

    public void setMang(String s) {
        card.mang=s;
    }

    public void setAmount(String s) {
        card.amount=Integer.valueOf(s);
    }

    public Card getCard() {
        return card;
    }

    public Card cloneCard() {
        return new Card(card);
    }

    public enum Stage{
        NONE,MANG,PRICE,SERI,PIN,DONE;
    }

    public Stage getStage() {
        return stage;
    }

    public String getSeri() {
        return card.seri;
    }

    public String getPin() {
        return card.pin;
    }

    public String getMang() {
        return card.mang;
    }

    public int getAmount() {
        return card.amount;
    }

    public Player getPlayer() {
        return player;
    }
}
