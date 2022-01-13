package me.may.chaos_plugin;

import com.sun.org.apache.xpath.internal.operations.String;
import jdk.internal.util.xml.impl.Input;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.HttpURLConnection;
import java.nio.Buffer;
import java.util.ArrayList;
import java.io.*;

public final class Chaos_plugin extends JavaPlugin {
    FileConfiguration config = getConfig();
    static String GET_URL = "http://localhost:8080";
    static ArrayList<Player> players = new ArrayList<>();
    static String current_request = "";

    private static final String USER_AGENT = "Java 17 MC Chaos Mod";

    private static void getFromURL() throws Exception {
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
            for (Player player : players) {
                task(response.toString(), player);
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
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        // load the config
        config.addDefault("delay", 10);
        config.options().copyDefaults(true);
        saveConfig();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    getFromURL();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0L, config.getInt("delay") * 20);
    }

    private static void task(String args, Player player) {
        // check if ';' is in string
        if (args.contains(";")) {
            String split_args[] = args.split(";");
            switch (split_args[0]) {
                case "tp":
                    break;

                case "summon":
                    int count = Integer.parseInt(split_args[1]);
                    String ent_type = split_args[2];
                    String ent_name = "";
                    if (split_args.length > 3) {
                        ent_name = split_args[3];
                    }

                    player.getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(ent_type));
                    

                    break;

            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        players.add(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        players.remove(e.getPlayer());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }
}
