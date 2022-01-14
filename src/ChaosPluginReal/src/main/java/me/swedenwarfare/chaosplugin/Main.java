package me.swedenwarfare.chaosplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public final class Main extends JavaPlugin implements Listener {
    FileConfiguration config = getConfig();
    static String playerName = "";
    static ArrayList<Player> players = new ArrayList<>();
    private static String GET_URL = "http://localhost:8080";

    @Override
    public void onEnable() {
        config.addDefault("delay",10);
        config.options().copyDefaults(true);
        saveConfig();
        //GET_URL = config.getString("url");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    sendHttpGETRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0L, config.getInt("delay")*20); //0 Tick initial delay, 20 Tick (1 Second) between repeats
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
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                task(response.toString().split(";"));
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
    void task(String[] args) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if (p != null) {
                String cmd = "";
                int duration = 0;
                String other = "";
                String player = "";
                String dest = "";
                boolean doRandom = false;
                int xZRange = 0;
                String xZRangeNegOrPlus = "";
                int yRange = 0;
                String YRangeNegOrPlus = "";
                if (args.length < 3) {
                    cmd = args[0];
                    try {
                        duration = Integer.parseInt(args[1]);

                    } catch (Exception e) {
                        other = args[1];
                    }


                } else if (args.length < 4) {
                    cmd = args[0];
                    duration = Integer.parseInt(args[1]);
                    other = args[2];
                } else if (args.length > 3 && args.length < 5) {
                    cmd = args[0];
                    duration = Integer.parseInt(args[1]);
                    other = args[2];
                    player = args[3];
                } else if (args.length > 5) {
                    cmd = args[0];
                    System.out.println("Cmd " + cmd);
                    duration = Integer.parseInt(args[1]);
                    System.out.println("Duration " + duration);
                    other = args[2];
                    System.out.println("Other" + other);
                    dest = args[3];
                    System.out.println("Dest " + dest);
                    doRandom = Boolean.valueOf(args[4]);
                    System.out.println("doRandom " + doRandom);
                    if (args[5].startsWith("-")) {
                        String[] temp = args[5].split("-");
                        System.out.println(temp[0]);
                        xZRangeNegOrPlus = "-";
                        xZRange = Integer.parseInt(temp[1]);
                    } else {
                        xZRange = Integer.parseInt(args[5]);
                    }
                    if (args[6].startsWith("-")) {
                        String[] temp = args[6].split("-");
                        YRangeNegOrPlus = "-";
                        yRange = Integer.parseInt(temp[1]);
                    } else {
                        yRange = Integer.parseInt(args[6]);
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
                    case "effect":
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
                        if (duration == 1) {
                            if (player.isEmpty()) {
                                p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.valueOf(other.toUpperCase(Locale.ROOT)));
                                break;
                            } else {
                                summonCustom(player, other);
                                break;
                            }
                        } else {
                            if (player.isEmpty()) {
                                for (int i = 0; i < duration; i++)
                                    p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.valueOf(other.toUpperCase(Locale.ROOT)));
                                break;
                            } else {
                                for (int i = 0; i < duration; i++)
                                    summonCustom(player, other);
                                break;
                            }
                        }
                    case "sound":
                        p.playSound(p.getLocation(), Sound.valueOf(other.toUpperCase(Locale.ROOT)), duration, Integer.parseInt(player));
                        break;
                    case "fill":
                        fillInv(other);
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
                        p.sendMessage(other);
                        break;
                    case "give":
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

                    case "armor":
                        ItemStack[] arr = {new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT))),new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT))),new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT))),new ItemStack(Material.getMaterial(other.toUpperCase(Locale.ROOT)))};
                        p.getInventory().setArmorContents(arr);
                        break;
                    case "fire":
                        p.setVisualFire(true);
                        break;
                    default:
                        break;

                }
            }
        }
    }
    void fillInv(String itemName){
        for(Player p : Bukkit.getOnlinePlayers()) {

            for (int i = 0; i < p.getInventory().getSize(); i++) {
                for (int x = 0; x < 64; x++)
                    p.getInventory().addItem(new ItemStack(Material.getMaterial(itemName.toUpperCase(Locale.ROOT))));
            }
        }
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
    void tempClear(int duration){
        for(Player p : Bukkit.getOnlinePlayers()) {

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
    }
    static void summonCustom(String name, String type){
        for(Player p : Bukkit.getOnlinePlayers()) {

            Entity e = p.getWorld().spawnEntity(p.getLocation(), EntityType.valueOf(type.toUpperCase(Locale.ROOT)));
            e.setCustomName(name);
            e.setCustomNameVisible(true);
        }

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
