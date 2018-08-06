package com.gmail.vkhanh234.napthe.data.plainfile;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 18/8/2015.
 */
public class LogFile {
    private PrintWriter out;
    String path = "plugins/napthe/";
    public LogFile(String name){
        File f = new File(path+name+".txt");
        try {
            if(!f.exists()) {
                f.createNewFile();
            }
            this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,true), "UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void log(String s){
        append(s);
    }
    public void append(String s){
        out.println(s);
        out.flush();
    }
    public void close(){
        out.flush();
        out.close();
    }
}
