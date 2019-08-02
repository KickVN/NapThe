package com.gmail.vkhanh234.napthe;

import com.gmail.vkhanh234.napthe.command.CommandManager;
import com.gmail.vkhanh234.napthe.config.GuiConfig;
import com.gmail.vkhanh234.napthe.config.MainConfig;
import com.gmail.vkhanh234.napthe.data.*;
import com.gmail.vkhanh234.napthe.gui.HistoryGui;
import com.gmail.vkhanh234.napthe.hook.Placeholder;
import com.gmail.vkhanh234.napthe.type.Type;
import com.gmail.vkhanh234.napthe.utils.KUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class NapThe extends JavaPlugin{
    Map<UUID, ChatStatus> map = new HashMap();
    public Type type;
    private static  NapThe plugin;
    private MainConfig mc;
    private GuiConfig guiConfig;
    private Data data;
    private PlayerController playerController;
    private CommandManager commandManager;
    public void onEnable()
    {
        this.plugin = this;
        initConfig();
        getServer().getPluginManager().registerEvents(new MainListener(), this);

        playerController = new PlayerController();
        loadPlayers();

        commandManager = new CommandManager();

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new Placeholder().hook();
        }
    }

    public void onDisable() {
        data.close();
    }

    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args)
    {
        commandManager.onCommand(sender,cmd,label,args);
        return true;
    }

    public void purge(CommandSender sender, long time) {
        final long timestamp = System.currentTimeMillis()-time;
        sender.sendMessage(getMessage("purge.message").replace("{date}", KUtils.getDateText(timestamp)));
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                Card base = new Card();
                base.seen = null;
                base.timestamp = timestamp;
                List<Card> cards = data.getCard(base);
                for(Card c:cards){
                    c.remove=true;
                    c.saved=false;
                    playerController.updateCard(c);
                }
                sender.sendMessage(getMessage("purge.done"));
            }
        });
    }

    public void showStatus(CommandSender sender) {
        sender.sendMessage(getMessage("status.message"));
        for(String s:mc.getNhamang().keySet()){
            String msg = getMessage("status.mang").replace("{mang}",mc.getNhamang().get(s).text).replace("{status}",getStatusText(s));
            sender.sendMessage(msg);
        }
    }

    public String getStatusText(String s){
        if(!mc.getNhamang().containsKey(s)) return getMessage("status.false");
        if(!mc.getNhamang().get(s).enable) return getMessage("status.false");
        else if(type.getStatus().containsKey(s)) return getMessage("status.true");
        else return getMessage("status.not_working");
    }

    public void showTop(final CommandSender sender, final int page, final long time) {
        sender.sendMessage(getMessage("top.loading"));
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                List<TopEntry> top = data.getTop(time);
                if(top==null) return;
                Collections.sort(top, new Comparator<TopEntry>() {
                    @Override
                    public int compare(TopEntry o1, TopEntry o2) {
                        return o2.amount-o1.amount;
                    }
                });
                int amount = mc.getRowPerPage();
                int maxPage = Double.valueOf(Math.ceil(top.size()*1.0/amount)).intValue();
                sender.sendMessage(getMessage("top.message").replace("{page}",page+"").replace("{total}",maxPage+""));
                if(page<1 || page>maxPage) return;
                for(int i=(page-1)*amount;(i<page*amount && i<top.size());i++){
                    TopEntry entry = top.get(i);
                    sender.sendMessage(getMessage("top.player").replace("{index}",(i+1)+"").replace("{player}",entry.name).replace("{value}",entry.amount+""));
                }
            }
        });
    }

    public void showSearch(CommandSender sender, String s, int page){
        sender.sendMessage(getMessage("search.loading"));
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                List<Card> list = searchCard(s);
                Collections.sort(list, new Comparator<Card>() {
                    @Override
                    public int compare(Card o1, Card o2) {
                        return Long.valueOf(o2.timestamp).compareTo(Long.valueOf(o1.timestamp));
                    }
                });

                int amount = mc.getRowPerPage();
                int maxPage = Double.valueOf(Math.ceil(list.size()*1.0/amount)).intValue();
                sender.sendMessage(getMessage("search.message").replace("{page}",page+"").replace("{total}",maxPage+""));
                if(page<1 || page>maxPage) return;
                for(int i=(page-1)*amount;(i<page*amount && i<list.size());i++){
                    Card c = list.get(i);
                    String msg = c.applyPlaceholder(getMessage("search.card"));
                    if(sender instanceof Player){
                        TextComponent message = new TextComponent(msg);
                        message.setClickEvent( new ClickEvent( ClickEvent.Action.SUGGEST_COMMAND, "/napthe setcardcode " + c.id+" "));
                        message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Nhấn vào để nhập nhanh lệnh setcardcode").create() ) );
                        ((Player) sender).spigot().sendMessage(message);
                    }
                    else sender.sendMessage(msg);
                }
            }
        });
    }

    public void showHistory(CommandSender sender, final OfflinePlayer p, final int page){
        if (p == null || !p.hasPlayedBefore()) {
            sender.sendMessage(getMessage("playerNotFound"));
            return;
        }
        sender.sendMessage(getMessage("history.loading"));
        boolean self = ((sender instanceof Player) && ((Player) sender).getUniqueId().equals(p.getUniqueId()));
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                PlayerData playerData = data.loadPlayer(p);
                if (playerData == null) {
                    sender.sendMessage(NapThe.getPlugin().getMessage("playerNotFound"));
                    return;
                }
                ArrayList<Card> list = new ArrayList<>(playerData.getCards().values());
                Collections.sort(list, new Comparator<Card>() {
                    @Override
                    public int compare(Card o1, Card o2) {
                        return Long.valueOf(o2.timestamp).compareTo(Long.valueOf(o1.timestamp));
                    }
                });

                if (mc.isEnableHistoryGui() && sender instanceof Player) {
                    Player p = (Player) sender;
                    HistoryGui gui = new HistoryGui(p, playerData, page, list);
                    playerController.getPlayerData(p).setGui(gui);
                } else {

                    int amount = mc.getRowPerPage();
                    int maxPage = Double.valueOf(Math.ceil(list.size() * 1.0 / amount)).intValue();
                    sender.sendMessage(getMessage("history.message").replace("{player}", p.getName()).replace("{page}", page + "").replace("{total}", maxPage + ""));
                    if (page < 1 || page > maxPage) return;
                    for (int i = (page - 1) * amount; (i < page * amount && i < list.size()); i++) {
                        Card c = list.get(i);
                        sender.sendMessage(c.applyPlaceholder(getMessage("history.card")));
                    }
                    if (self) {
                        Player p = (Player) sender;
                        for (Card c : list) {
                            if (!c.seen) notifyCard(p, c);
                        }
                    }
                }
            }
        });
    }

    public List<Card> searchCard(String s){
        Card base = getCardFromArgs(s);
        return data.getCard(base);
    }


    public Card getCardFromArgs(String s){
        HashMap<String,String> map = new HashMap<>();
        String[] spl = s.split(" ");
        int i=0;
        while(i<spl.length){
            if(spl[i].startsWith("-")){
                String res="";
                for(int j=i+1;j<spl.length;j++){
                    if(spl[j].startsWith("-")) break;
                    res+=spl[j]+" ";
                }
                res = res.substring(0,res.length()-1);
                map.put(spl[i].substring(1),res);
                i+=2;
            }
            else i++;
        }
        return new Card(map);
    }


    public ChatStatus getCard(Player p) {
        return this.map.containsKey(p.getUniqueId())?(ChatStatus)this.map.get(p.getUniqueId()):null;
    }

    public void napThe(final Player p)
    {
        final ChatStatus c = getCard(p);
        if(c==null || !c.stage.equals(ChatStatus.Stage.DONE)){
            this.map.remove(p.getUniqueId());
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                Card r;
                try {
                    r = type.send(c);
                } catch(Exception e){
                    p.sendMessage(ChatColor.RED+"Lỗi hệ thống! Config sai!");
                    e.printStackTrace();
                    return;
                }
                if(r==null){
                    p.sendMessage(ChatColor.RED+"Lỗi hệ thống! Config sai!");
                    return;
                }
                notifyCard(p,r);
                playerController.addCard(p,r);
                playerController.savePlayer(p);



            }
        });

    }

    private void notifyCard(Player p, Card r) {
        if (r.isCorrect()) {
            if(r.amount>0) {
                sendPrize(p, r.amount);
            }
        }
        p.sendMessage(mc.getResponse(r));
        if(!r.seen) {
            r.seen = true;
            r.saved = false;
            Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    playerController.updateCard(p, r);
                }
            });
        }
    }

    public void sendPrize(final Player p, final int amount) {
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                List<String> cmd = mc.getPrize(String.valueOf(amount/1000));
                for (String s:cmd){
                    getServer().dispatchCommand(getServer().getConsoleSender(), s.replace("{player}",p.getName()));
                }
            }

        });
    }

    private void loadPlayers() {
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                for(Player p:Bukkit.getOnlinePlayers()) playerController.loadPlayer(p);
            }
        });
    }

    public void activeMode(Player p, String mang, boolean showprice)
    {
        ChatStatus c = new ChatStatus();
        c.setMang(mang);
        if(mc.isPriceEnable()) {
            c.stage = ChatStatus.Stage.PRICE;
            if(showprice){
                sendPriceChooser(p);
            }
        }
        else {
            c.stage = ChatStatus.Stage.SERI;
            p.sendMessage(getMessage("seri"));
        }
        c.player = p;
        this.map.put(p.getUniqueId(),c);
    }

    public void choosePrice(Player p, String s) {
        ChatStatus c = getCard(p);
        if(c==null || !c.stage.equals(ChatStatus.Stage.PRICE)) return;
        c.setAmount(s);
        c.stage = ChatStatus.Stage.SERI;
        p.sendMessage(getMessage("seri"));
    }

    public String getMessage(String m)
    {
        return mc.getMessage(m);
    }

    public void sendChooser(Player p)
    {
        p.sendMessage(getMessage("choose"));
        for (String s : type.getStatus().keySet()) {
            TextComponent message = new TextComponent(type.getStatus().get(s));
            message.setColor(net.md_5.bungee.api.ChatColor.AQUA);
            message.setBold(true);
            message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/napthe choose " + s));
            message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getMessage("hover")).create() ) );
            p.spigot().sendMessage(message);
        }
    }

    public void sendPriceChooser(Player p)
    {
        p.sendMessage(getMessage("pricechoose"));
        for (String s : mc.getPrices().keySet()) {
            TextComponent message = new TextComponent(mc.getPrices().get(s));
            message.setColor(net.md_5.bungee.api.ChatColor.AQUA);
            message.setBold(true);
            message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/napthe price " + s));
            message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getMessage("hover")).create() ) );
            p.spigot().sendMessage(message);
        }
    }

    public void setCardCode(CommandSender sender, String id, int code) {
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                Card c = data.getCard(id);
                if(c==null){
                    sender.sendMessage(getMessage("fail"));
                    return;
                }
                c.setCode(code);
                if(playerController.updateCard(c)) sender.sendMessage(getMessage("success"));
                else sender.sendMessage(getMessage("fail"));
            }
        });
    }

    public void initConfig()
    {
        mc = new MainConfig();
        mc.load();

        guiConfig = new GuiConfig();
    }


    public void setType(Type type) {
        this.type = type;
    }

    public static NapThe getPlugin(){
        return plugin;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public MainConfig getMC() {
        return mc;
    }

    public Data getData() {
        return data;
    }

    public PlayerController getPC() {
        return playerController;
    }

    public Map<UUID, ChatStatus> getCardMap() {
        return map;
    }

    public Type getType() {
        return type;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public GuiConfig getGuiConfig() {
        return guiConfig;
    }
}
