package com.gmail.vkhanh234.napthe;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by Admin on 24/7/2015.
 */
public class KUtils {
    private static Random random = new Random();
    public static double getPercent(int score, int total){
        return round((1.0*score/total)*100,2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static int getRandom(String s) {
        if(s.contains("-")){
            String[] spl = s.split("-");
            return randomNumber(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]));
        }
        else return Integer.parseInt(s);
    }

    public static int randomNumber(int min,int max){
        return random.nextInt((max - min)+1) + min;
    }
    public static boolean hasPermmision(Player p, String perm){
        if(p.hasPermission(perm)) return true;
        if(p.isOp()) return true;
        return false;
    }
    public static boolean isSuccess(int percent) {
        int g = randomNumber(1,100);
        if (g<=percent) return true;
        return false;
    }
    public static String backColor(String name) {
        return name.replace("\u00a7","&");
    }
    public static String convertColor(String name){
        return name==null?null:name.replace("&","\u00a7");
    }

    public static String getDateText(long timestamp) {
        Date date = new Date(timestamp);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return dateFormat.format(date);
    }
}
