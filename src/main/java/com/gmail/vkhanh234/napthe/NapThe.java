package com.gmail.vkhanh234.napthe;

import com.gmail.vkhanh234.napthe.data.*;
import com.gmail.vkhanh234.napthe.type.Type;
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
    private Data data;
    private PlayerController playerController;

    public void onEnable()
    {
        this.plugin = this;
        initConfig();
        getServer().getPluginManager().registerEvents(new MainListener(), this);

        playerController = new PlayerController();
        loadPlayers();
    }

    public void onDisable() {
        data.close();
    }

    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args)
    {
        if(hasPermission(sender, "command")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload") && hasPermission(sender, "admin")) {
                    reloadConfig();
                    initConfig();
                    sender.sendMessage(ChatColor.GREEN + "Reload thành công!");
                } else if (args.length>=2 && (args[0].equalsIgnoreCase("choose")) &&
                        (type.getStatus().containsKey(args[1].toUpperCase()))) {
                    activeMode((Player) sender, args[1],args.length>=3?Boolean.parseBoolean(args[2]):true);
                }
                else if(args.length>=2 && (args[0].equalsIgnoreCase("price")) && mc.getPrices().containsKey(args[1])){
                    choosePrice((Player) sender,args[1]);
                }
                else if (args.length==3 && (args[0].equalsIgnoreCase("give"))  && hasPermission(sender, "admin")){
                    Player p = Bukkit.getPlayer(args[1]);
                    if(p!=null){
                        sendPrize(p,Integer.parseInt(args[2]));
                    }
                }
                else if(args[0].equalsIgnoreCase("top") && data.isDetailAvailable()){
                    int page = args.length>=2?Integer.valueOf(args[1]):1;
                    long time = args.length>=3?Long.valueOf(args[2])*1000:System.currentTimeMillis();
                    showTop(sender,page,time);
                }
                else if(args[0].equalsIgnoreCase("lichsu") && data.isDetailAvailable()){
                    if(args.length>=2 && !args[1].matches("\\d+")){
                        if(hasPermission(sender,"admin")) showHistory(sender,Bukkit.getOfflinePlayer(args[1]), args.length >= 3 ? Integer.valueOf(args[2]):1);
                    }
                    else {
                        int page = args.length >= 2 ? Integer.valueOf(args[1]) : 1;
                        showHistory(sender,(Player) sender, page);
                    }
                }
                else if(args[0].equalsIgnoreCase("purge") && data.isDetailAvailable() && hasPermission(sender,"admin") && args.length>=2){
                    long time = Long.parseLong(args[1])*1000;
                    purge(sender,time);
                }
                else if(args[0].equalsIgnoreCase("timkiem") && data.isDetailAvailable() && hasPermission(sender,"admin") && args.length>=3){
                    int start = args[1].startsWith("-")?1:2;
                    String s = args[start];
                    for(int i=start+1;i<args.length;i++) s+=" "+args[i];
                    showSearch(sender, s,start==1?1:Integer.valueOf(args[1]));
                }
                else if(args[0].equalsIgnoreCase("thongke") && hasPermission(sender, "admin")){
                    final long time = args.length>=2?Long.valueOf(args[1])*1000:System.currentTimeMillis();
                    Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
                        @Override
                        public void run() {
                            sender.sendMessage(getMessage("total").replace("{value}",data.getTotalAmount(time)+""));
                        }
                    });
                }
                else if(args[0].equalsIgnoreCase("setcardcode") && hasPermission(sender,"admin") && args.length>=3 && args[2].matches("\\d+")){
                    String id = args[1];
                    int code = Integer.valueOf(args[2]);
                    setCardCode(sender,id,code);
                }
                else if(args[0].equalsIgnoreCase("info")){
                    sender.sendMessage(ChatColor.GREEN+"Plugin "+ChatColor.AQUA+ChatColor.BOLD+"napthe "+ChatColor.GREEN
                    +"phiên bản "+ChatColor.AQUA+ChatColor.BOLD+getDescription().getVersion()+ChatColor.GREEN+" được làm bởi "+ChatColor.AQUA+ChatColor.BOLD+"KickVN");
                }
                else{
                    showHelp(sender);
                }
            } else {
                sendChooser((Player) sender);
            }
        }

        return true;
    }

    private void purge(CommandSender sender, long time) {
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

    private void showHelp(CommandSender sender) {
        if(hasPermission(sender,"command")){
            if(data.isDetailAvailable()) sender.sendMessage(ChatColor.GOLD+"/napthe top [page] [seconds]"+ChatColor.RESET+" - xem Top nạp thẻ (trong khoảng thời gian)");
            if(data.isDetailAvailable()) sender.sendMessage(ChatColor.GOLD+"/napthe lichsu [page]"+ChatColor.RESET+" - xem lịch sử nạp thẻ và nhận thưởng");
            sender.sendMessage(ChatColor.GOLD+"/napthe info"+ChatColor.RESET+" - thông tin Plugin");
        }
        if(hasPermission(sender,"admin")){
            sender.sendMessage(ChatColor.GOLD+"/napthe choose <nhà_mạng> [true/false]"+ChatColor.RESET+" - chọn nhà mạng. True/false để tắt/bật hiển thị lựa chọn cho mệnh giá.");
            sender.sendMessage(ChatColor.GOLD+"/napthe price <giá_tiền>"+ChatColor.RESET+" - chọn giá tiền. Sử dụng sau khi chọn mạng.");
            sender.sendMessage(ChatColor.GOLD+"/napthe reload"+ChatColor.RESET+" - reload config");
            if(data.isDetailAvailable()) sender.sendMessage(ChatColor.GOLD+"/napthe thongke [seconds]"+ChatColor.RESET+" - thống kê tổng tiền (trong khoảng thời gian)");
            if(data.isDetailAvailable()) sender.sendMessage(ChatColor.GOLD+"/napthe lichsu [player]"+ChatColor.RESET+" - xem lịch sử nạp thẻ của người chơi khác");
            if(data.isDetailAvailable()) sender.sendMessage(ChatColor.GOLD+"/napthe timkiem [page] [query]"+ChatColor.RESET+" - tìm card");
            if(data.isDetailAvailable()) sender.sendMessage(ChatColor.GOLD+"/napthe purge [seconds]"+ChatColor.RESET+" - xóa dữ liệu đã tồn tại lâu hơn khoảng thời gian [seconds]");
            sender.sendMessage(ChatColor.GOLD+"/napthe give <player> <tiền>"+ChatColor.RESET+" - trao phần thưởng cho người chơi");
        }
    }

    private boolean hasPermission(CommandSender sender, String p) {
        if(sender.isOp()) return true;
        if(sender.hasPermission("napthe."+p)) return true;
        sender.sendMessage(getMessage("noPerm"));
        return false;
    }

//    private void showStatus(CommandSender sender) {
//        sender.sendMessage(getMessage("status.message"));
//        for(String s:mc.getNhamang().keySet()){
//            String msg = getMessage("status.mang").replace("{mang}",mc.getNhamang().get(s));
//            if(type.getStatus().containsKey(s)) sender.sendMessage(msg.replace("{status}",getMessage("status.true")));
//            else sender.sendMessage(msg.replace("{status}",getMessage("status.false")));
//        }
//    }

    public void showTop(final CommandSender sender, final int page, final long time) {
        sender.sendMessage("top.loading");
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                List<TopEntry> top = data.getTop(time);
                if(top==null) return;
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

    public void showHistory(CommandSender sender, final OfflinePlayer p, final int page){
        sender.sendMessage(getMessage("history.loading"));
        boolean self = ((sender instanceof Player) && ((Player) sender).getUniqueId().equals(p.getUniqueId()));
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                PlayerData playerData = p.isOnline()?playerController.getPlayerData((Player) p):data.loadPlayer(p);
                if(playerData==null) return;
                ArrayList<Card> list = new ArrayList<>(playerData.getCards().values());
                Collections.reverse(list);
                int amount = mc.getRowPerPage();
                int maxPage = Double.valueOf(Math.ceil(list.size()*1.0/amount)).intValue();
                sender.sendMessage(getMessage("history.message").replace("{player}",p.getName()).replace("{page}",page+"").replace("{total}",maxPage+""));
                if(page<1 || page>maxPage) return;
                for(int i=(page-1)*amount;(i<page*amount && i<list.size());i++){
                    Card c = list.get(i);
                    sender.sendMessage(c.applyPlaceholder(getMessage("history.card")));
                }
                if(self){
                    Player p = (Player) sender;
                    for(Card c:list){
                        if(!c.seen) notifyCard(p,c);
                    }
                }
            }
        });
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

    private void sendPrize(final Player p, final int amount) {
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

    private void activeMode(Player p, String mang, boolean showprice)
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

    private void choosePrice(Player p, String s) {
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

    private void setCardCode(CommandSender sender,String id, int code) {
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

    private void initConfig()
    {
        mc = new MainConfig();
        mc.load();
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
}
