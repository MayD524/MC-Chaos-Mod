package me.swedenwarfare.chaosplugin;

/*
    Author: Sweden
     - (later) May
    version 1.0b4
    May : 1/15/2022
     - added enchantments
     - changed fillInv it now takes player as a parameter
     - added santaSays
*/
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;

import java.util.concurrent.TimeUnit;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.io.*;

public final class Main extends JavaPlugin implements Listener {
    FileConfiguration config         = getConfig();
    static String playerName         = "";
    static ArrayList<Player> players = new ArrayList<>();
    private static String GET_URL    = "http://localhost:8080";

    @Override
    public void onEnable() {
        config.addDefault("delay",10);
        config.options().copyDefaults(true);
        saveConfig();
        //GET_URL = config.getString("url");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() == 0 ) { return; }
                try {
                    sendHttpGETRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // changed this to run every tick
        }, 0L, config.getInt("delay") * 20); //0 Tick initial delay, 20 Tick (1 Second) between repeats
        // Enable our class to check for new players using onPlayerJoin()
        getServer().getPluginManager().registerEvents(this, this);
    }
    private static final String USER_AGENT = "Mozilla/5.0";

    private void sendHttpGETRequest(){

        try
        {
            URL obj = new URL(GET_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);

            //if (responseCode == 400) {
            //    TimeUnit.SECONDS.sleep(5);
            //} else if (responseCode == 404) {
            //    TimeUnit.SECONDS.sleep(20);
            //}

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                System.out.println(response.toString());
                if (response.toString().contains("|")) {
                    task(response.toString().split("\\|"));
                } else {
                    task(new String[]{response.toString()});
                }
                in.close();
                // print result
                System.out.println(response.toString());
            } else {
                System.out.println("GET request not worked");
            }

            for (int i = 1; i <= 8; i++) {
                System.out.println(httpURLConnection.getHeaderFieldKey(i) + " = " + httpURLConnection.getHeaderField(i));
            }
        }catch (Exception e){}
    }

    //Player p = Bukkit.getPlayer(playerName);
    void task(String[] http_args) {

        for (String arg : http_args)
        {
            String[] args = arg.split(";");
            // just in case
            if (args.length == 0) { continue; }

            if (args[0].equalsIgnoreCase("delay")) {
                int delayFor = Integer.parseInt(args[1]);
                // wait for delay seconds
                // TODO: Maybe make this work lol
                try {
                    TimeUnit.SECONDS.sleep(delayFor);
                } catch (InterruptedException e) {}
                System.out.println("Waiting for " + delayFor + " seconds");

                System.out.println("Done waiting");
                continue;
            }

            for(Player p : Bukkit.getOnlinePlayers()) {
                boolean doRandom        = false;
                String xZRangeNegOrPlus = "";
                String YRangeNegOrPlus  = "";
                String player           = "";
                String other            = "";
                String dest             = "";
                String cmd              = "";
                int duration            = 0;
                int xZRange             = 0;
                int yRange              = 0;

                if (args.length < 3) {
                    cmd = args[0];
                    try {
                        duration = Integer.parseInt(args[1]);

                    } catch (Exception e) {
                        other = args[1];
                    }

                } else if (args.length < 4) {
                    cmd      = args[0];
                    duration = Integer.parseInt(args[1]);
                    other    = args[2];

                } else if (args.length > 3 && args.length < 5) {
                    cmd      = args[0];
                    duration = Integer.parseInt(args[1]);
                    other    = args[2];
                    player   = args[3];

                } else if (args.length > 4 && args.length < 6) {
                    cmd      = args[0];
                    other    = args[1];
                    duration = Integer.parseInt(args[2]);
                    xZRange  = Integer.parseInt(args[3]);
                    yRange   = Integer.parseInt(args[4]);

                } else if (args.length > 6) {
                    cmd      = args[0];
                    System.out.println("Cmd " + cmd);
                    duration = Integer.parseInt(args[1]);
                    System.out.println("Duration " + duration);
                    other    = args[2];
                    System.out.println("Other" + other);
                    dest     = args[3];
                    System.out.println("Dest " + dest);
                    doRandom = Boolean.valueOf(args[4]);
                    System.out.println("doRandom " + doRandom);

                    if (args[5].startsWith("-")) {
                        String[] temp    = args[5].split("-");
                        System.out.println(temp[0]);
                        xZRangeNegOrPlus = "-";
                        xZRange          = Integer.parseInt(temp[1]);
                    } else {
                        xZRange          = Integer.parseInt(args[5]);
                    }

                    if (args[6].startsWith("-")) {
                        String[] temp    = args[6].split("-");
                        YRangeNegOrPlus  = "-";
                        yRange           = Integer.parseInt(temp[1]);
                    } else {
                        yRange           = Integer.parseInt(args[6]);
                    }
                }

                switch (cmd) {
                    case "tp":
                        if (other.equalsIgnoreCase("player")) {
                            if (doRandom) {
                                Random r = new Random();
                                if (xZRangeNegOrPlus.equalsIgnoreCase("-")) {
                                    int i = r.nextInt(2);
                                    int randXZ = r.nextInt(xZRange);
                                    System.out.println("I " + i);
                                    if (i == 1)
                                        randXZ *= -1;
                                    if (YRangeNegOrPlus.equalsIgnoreCase("-")) {
                                        int ii = r.nextInt(2);
                                        int randY = r.nextInt(yRange);
                                        if (i == 1)
                                            randY *= -1;
                                        p.teleport(new Location(p.getWorld(), p.getLocation().getX() + randXZ, p.getLocation().getY() + randY, p.getLocation().getZ() + randXZ));
                                        break;
                                    } else {
                                        int randY = r.nextInt(yRange);
                                        p.teleport(new Location(p.getWorld(), p.getLocation().getX() + randXZ, p.getLocation().getY() + randY, p.getLocation().getZ() + randXZ));
                                        break;
                                    }
                                } else {
                                    if (YRangeNegOrPlus.equalsIgnoreCase("-")) {
                                        int randXZ = r.nextInt(xZRange);
                                        int i = r.nextInt(2);
                                        int randY = r.nextInt(yRange);
                                        System.out.println("I " + i);
                                        if (i == 1)
                                            randY *= -1;
                                        p.teleport(new Location(p.getWorld(), p.getLocation().getX() + randXZ, p.getLocation().getY() + randY, p.getLocation().getZ() + randXZ));
                                        break;
                                    } else {
                                        int randXZ = r.nextInt(xZRange);
                                        int randY = r.nextInt(yRange);
                                        p.teleport(new Location(p.getWorld(), p.getLocation().getX() + randXZ, p.getLocation().getY() + randY, p.getLocation().getZ() + randXZ));
                                        break;
                                    }
                                }
                            } else {
                                if (xZRangeNegOrPlus.isEmpty())
                                    if (YRangeNegOrPlus.isEmpty()) {
                                        p.teleport(new Location(p.getWorld(), p.getLocation().getX() + xZRange, p.getLocation().getY() + yRange, p.getLocation().getZ() + xZRange));
                                        break;
                                    } else {
                                        p.teleport(new Location(p.getWorld(), p.getLocation().getX() + xZRange, p.getLocation().getY() - yRange, p.getLocation().getZ() + xZRange));
                                        break;
                                    }

                                else if (YRangeNegOrPlus.isEmpty()) {
                                    p.teleport(new Location(p.getWorld(), p.getLocation().getX() - xZRange, p.getLocation().getY() + yRange, p.getLocation().getZ() - xZRange));
                                    break;
                                } else {
                                    p.teleport(new Location(p.getWorld(), p.getLocation().getX() - xZRange, p.getLocation().getY() - yRange, p.getLocation().getZ() - xZRange));
                                    break;
                                }
                            }
                        }

                    case "santa":
                        santaSays(p);
                        break;

                    case "weather":
                        // weather;duration;type
                        switch (other) {
                            case "rain":
                                p.getWorld().setStorm(true);
                                p.getWorld().setThundering(true);
                                break;
                            case "thunder":
                                p.getWorld().setStorm(false);
                                p.getWorld().setThundering(true);
                                break;
                            case "rain_thunder":
                                p.getWorld().setStorm(true);
                                p.getWorld().setThundering(true);
                                break;
                            default:
                                p.getWorld().setStorm(false);
                                p.getWorld().setThundering(false);
                                break;
                        }
                        // update in ticks
                        p.getWorld().setWeatherDuration(duration * 20);
                        break;

                    case "time":
                        // time;type
                        switch (other) {
                            case "day":
                                p.getWorld().setTime(0);
                                break;
                            case "night":
                                p.getWorld().setTime(14000);
                                break;
                            case "midnight":
                                p.getWorld().setTime(18000);
                                break;
                            case "noon":
                                p.getWorld().setTime(6000);
                                break;
                            default:
                                p.getWorld().setTime(0);
                                break;
                        }
                        break;


                    case "smite":
                        // smite;[0-1]
                        if (duration == 0) {
                            p.getWorld().strikeLightning(p.getLocation());
                            break;
                        }
                        p.getWorld().strikeLightningEffect(p.getLocation());
                        break;

                    case "enchant":
                        // enchant;level;name
                        if (other.equalsIgnoreCase("doRandom"))
                        {
                            // enchant with a random enchantment
                            Random r = new Random();
                            int totalEnchants = Enchantment.values().length;
                            int randEnchant = r.nextInt(totalEnchants);
                            Enchantment ench = Enchantment.values()[randEnchant];
                            int level = r.nextInt(100);
                            p.getItemInUse().addUnsafeEnchantment(ench, level);
                            break;
                        }

                        switch (other.toLowerCase()) {
                            case "sharpness":
                                other = "DAMAGE_ALL";
                                break;
                            case "smite":
                                other = "DAMAGE_UNDEAD";
                                break;
                            case "bane of arthropods":
                                other = "DAMAGE_ARTHROPODS";
                                break;
                            case "unbreaking":
                                other = "DURABILITY";
                                break;
                            case "looting":
                                other = "LOOT_BONUS_MOBS";
                                break;
                            case "power":
                                other = "ARROW_DAMAGE";
                                break;
                            case "punch":
                                other = "ARROW_KNOCKBACK";
                                break;
                            case "flame":
                                other = "ARROW_FIRE";
                                break;
                        }
                        p.getItemInUse().addUnsafeEnchantment(Enchantment.getByName(other.toUpperCase(Locale.ROOT)), duration);
                        break;

                    case "effect":
                        // effect;duration;name;strength
                        if (other.equalsIgnoreCase("doRandom"))
                        {
                            System.out.println("Random effect");
                            // give the player a random effect
                            Random r = new Random();
                            int totalEffects = PotionEffectType.values().length;
                            int effect = r.nextInt(totalEffects);
                            PotionEffectType pot = PotionEffectType.values()[effect];
                            int level = r.nextInt(100);
                            duration = r.nextInt(100);
                            p.addPotionEffect(new PotionEffect(pot, duration, level));
                            break;
                        }

                        if (other.equalsIgnoreCase("haste"))
                            other = "FAST_DIGGING";
                        else if (other.equalsIgnoreCase("strength"))
                            other = "INCREASE_DAMAGE";
                        else if (other.equalsIgnoreCase("resistance"))
                            other = "DAMAGE_RESISTANCE";
                        else if (other.equalsIgnoreCase("nausea"))
                            other = "CONFUSION";
                        else if (other.equalsIgnoreCase("slowness"))
                            other = "SLOW";
                        p.addPotionEffect((new PotionEffect(PotionEffectType.getByName(other.toUpperCase(Locale.ROOT)), duration * 20, Integer.parseInt(player))));
                        break;

                    case "summon":
                        // summon;amount;name;customName
                        switch (other) {
                            case "doRandom":
                                Random r = new Random();
                                int totalEntities = EntityType.values().length;
                                int entity = r.nextInt(totalEntities);
                                EntityType ent = EntityType.values()[entity];
                                p.getWorld().spawnEntity(p.getLocation(), ent);
                                break;

                            case "tnt":
                                for (int i = 0; i < duration; i++) {
                                    summonTNT(p);
                                }
                                break;

                            case "puppy":
                                for (int i = 0; i < duration; i++) {
                                    if (player.isEmpty())
                                        tamedWolf(p, null);
                                    else
                                        tamedWolf(p, player);
                                }
                                break;

                            default:
                                if (duration == 1) {
                                    if (player.isEmpty()) {
                                        p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.valueOf(other.toUpperCase(Locale.ROOT)));
                                        break;
                                    } else {
                                        summonCustom(p, player, other);
                                        break;
                                    }
                                } else {
                                    if (player.isEmpty()) {
                                        for (int i = 0; i < duration; i++)
                                            p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.valueOf(other.toUpperCase(Locale.ROOT)));
                                        break;
                                    } else {
                                        for (int i = 0; i < duration; i++)
                                            summonCustom(p, player, other);
                                        break;
                                    }
                                }
                        }

                    case "sound":
                        if (other.equalsIgnoreCase("doRandom")) {
                            // play a random sound
                            Random r = new Random();
                            int totalSounds = Sound.values().length;
                            int sound = r.nextInt(totalSounds);
                            Sound s = Sound.values()[sound];
                            p.getWorld().playSound(p.getLocation(), s, 1, 1);
                            break;
                        }
                        p.playSound(p.getLocation(), Sound.valueOf(other.toUpperCase(Locale.ROOT)), duration, Integer.parseInt(player));
                        break;

                    case "fill":
                        fillInv(p, other);
                        break;

                    case "damage":
                        p.damage(duration);
                        break;

                    case "clear":
                        if (duration == 0)
                            return;
                        else {
                            tempClear(duration);
                            break;
                        }

                    case "msg":

                        p.sendMessage(ChatColor.translateAlternateColorCodes('&',other));
                        break;

                    case "give":
                        if (other.equalsIgnoreCase("doRandom")) {
                            // give a random item
                            Random r = new Random();
                            int totalItems = Material.values().length;
                            int item = r.nextInt(totalItems);
                            int amount = r.nextInt(100);
                            Material mat = Material.values()[item];
                            p.getInventory().addItem(new ItemStack(mat, amount));
                            break;
                        }
                        if (other.equalsIgnoreCase("head")) {
                            giveHead(p, player);
                            break;
                        }

                        if (Material.getMaterial(args[2].toUpperCase(Locale.ROOT)) != null) {
                            if (player.isEmpty()) {
                                ItemStack is = new ItemStack(Material.getMaterial(args[2].toUpperCase(Locale.ROOT)));
                                for (int i = 0; i < duration; i++) {
                                    p.getInventory().addItem(is);
                                }
                                break;
                            } else {
                                for (int i = 0; i < duration; i++) {
                                    p.getInventory().addItem(createItem(player, other.toUpperCase(Locale.ROOT)));
                                }
                                break;
                            }
                        }

                    case "place":
                        // place;BLOCK;x;y;z
                        placeBlock(p,duration,xZRange,yRange,Material.getMaterial(other.toUpperCase(Locale.ROOT)));
                        break;

                    case "armor":
                        // armor;slot;item
                        if (duration == 5){
                            ItemStack[] arr = {new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT))),new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT))),new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT))),new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT)))};
                            p.getInventory().setArmorContents(arr);
                        } else {
                            ItemStack[] playerArmor = p.getInventory().getArmorContents();
                            switch (duration){
                                case 1:
                                    playerArmor[0] = new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT)));
                                    break;
                                case 2:
                                    playerArmor[1] = new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT)));
                                    break;
                                case 3:
                                    playerArmor[2] = new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT)));
                                    break;
                                case 4:
                                    playerArmor[3] = new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT)));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case "closet":

                        break;
                    case "fire":
                        p.setVisualFire(true);
                        break;
                    case "trade":
                        //trade;0
                        Random r = new Random();
                        int itemTrade = r.nextInt(p.getInventory().getSize());
                        ItemStack item = tradeOffer(p,itemTrade);
                        while(p.getInventory().getItem(itemTrade).getType() == Material.AIR)
                            itemTrade = r.nextInt(p.getInventory().getSize());
                        //Maybe this one
                        while (item.getType() == Material.AIR)
                            item = tradeOffer(p,itemTrade);
                        p.sendMessage(item.getType().toString());
                        p.getInventory().getItem(itemTrade).setAmount(p.getInventory().getItem(itemTrade).getAmount()-p.getInventory().getItem(itemTrade).getAmount());
                        p.getInventory().addItem(item);
                        break;
                    default:
                        break;

                }
            }
        }
    }

    ItemStack tradeOffer(Player p, int slot){
        Random r = new Random();
        int totalItems = Material.values().length;
        int item = r.nextInt(totalItems);
        int amount = r.nextInt(64);
        Material mat = Material.values()[item];

        return new ItemStack(mat,amount);
    }
    void fillInv(Player p, String itemName){
        // removed the for loop here (we get the player earlier)
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            for (int x = 0; x < 64; x++)
                p.getInventory().addItem(new ItemStack(Material.getMaterial(itemName.toUpperCase(Locale.ROOT))));
        }

    }

    void tamedWolf(Player p, String name) {
        Wolf w = (Wolf) p.getWorld().spawnEntity(p.getLocation(), EntityType.WOLF);
        w.setTamed(true);
        if (name != null)
            w.setCustomName(name);
        w.setOwner(p);
    }

    void cage(int duration){
        Player p = Bukkit.getPlayer(playerName);
        double x = p.getLocation().getX();
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();

        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

            }
        }, 20*duration);
    }

    void santaSays(Player p) {
        // get a random number (is good or not)
        Random r = new Random();
        int isNice = r.nextInt(2);
        clearInv(p);

        // if it is good
        if (isNice == 1) {
            // fill inventory with diamonds
            fillInv(p, "DIAMOND");
            return;
        }

        // fill inventory with coal
        fillInv(p, "COAL");
    }

    void tempClear(Player p, int duration){
        

        ItemStack[] items = p.getInventory().getStorageContents();
        p.getInventory().clear();
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                for (ItemStack i : items)
                    if (i != null)
                        p.getInventory().addItem(i);
            }
        }, 20 * duration);
    }

    void giveHead(Player p, String playerName) { // it does not literally give head ... ;)
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta  = (SkullMeta) skull.getItemMeta();
        meta.setOwner(playerName);
        skull.setItemMeta(meta);
        p.getInventory().addItem(skull);
    }

    void clearInv(Player p) {
        ItemStack[] items = p.getInventory().getStorageContents();
        p.getInventory().clear();
    }

    void placeBlock(Player p, int x, int y, int z,Material block) {
        // place a block at a specific block relitve to the player
        p.getLocation().add(x,y,z).getBlock().setType(block);
        return;
    }

    static void summonTNT(Player p) {
        // summon a TNT entity
        p.getWorld().spawnEntity(p.getLocation(), EntityType.PRIMED_TNT);
    }

    static void summonCustom(Player p, String name, String type){
        Entity e = p.getWorld().spawnEntity(p.getLocation(), EntityType.valueOf(type.toUpperCase(Locale.ROOT)));
        e.setCustomName(name);
        e.setCustomNameVisible(true);
    }

    static ItemStack createItem(String itemName,String type){
        ItemStack item = new ItemStack(Material.valueOf(type));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemName);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        e.getEntity().setVisualFire(false);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e ){
        players.add(e.getPlayer());
        e.getPlayer().sendMessage("Welcome to hell! This is the Chaos Twitch Minecraft Plugin by the Devs at Char(69) Dev Team :). We hope you enjoy this :>");
        e.getPlayer().sendMessage("*Note this is in early access so please be patient things may not work correctly (version: 1.0b4)");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        players.remove(e.getPlayer());
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}