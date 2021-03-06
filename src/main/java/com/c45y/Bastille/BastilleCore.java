/*
 * The MIT License
 *
 * Copyright 2015 c45y.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.c45y.Bastille;

import com.c45y.Bastille.boss.*;
import com.c45y.Bastille.command.BastilleCommand;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BastilleCore extends JavaPlugin {
    private static BastilleCore _instance;
    private BastilleListener _listener;
    private HashMap<String, BastilleBoss> _bosses;
    
    private Random pluginRandom = new Random();  
    public final ChatColor _pluginColor = ChatColor.GREEN;
    
    public static BastilleCore getInstance() {
        return _instance;
    }

    public int getRandomChance() {
        return pluginRandom.nextInt(1000);
    }
    
    @Override
    public void onEnable() {
        /* Create a static reference to ourself */
        _instance = this;
                
        _bosses = new HashMap<String, BastilleBoss>();
        /* Register our boss spawners */
        registerBoss("SirMeowingtons", new SirMeowingtons(this));
        registerBoss("LordPuggleston", new LordPuggleston(this));
        registerBoss("DrCuddles", new DrCuddles(this));
        registerBoss("MrSkeletal", new MrSkeletal(this));
        
        /* Register our listener(s) */
        _listener = new BastilleListener(this);
        getServer().getPluginManager().registerEvents(_listener, this);
        
        getCommand("bastille").setExecutor(new BastilleCommand(this));
    }
    
    public void registerBoss(String name, BastilleBoss boss) {
        _bosses.put(name.toLowerCase(), boss);
    }
    
    public BastilleBoss getBoss(String name) {
        if (_bosses.containsKey(name.toLowerCase())) {
            return _bosses.get(name.toLowerCase());
        }
        return null;
    }
    
    public Set<String> getBosses() {
        return _bosses.keySet();
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }
    
    public boolean spawnBoss(String name, Location location) {       
        BastilleBoss boss = getBoss(name);
        if (boss == null) {
            return false;
        }
        
        boss.spawn(location);
        return true;
    }
    
    /**
     * Player communication function, uses our defined 'plugin color'
     * @param player
     * @param message
     */
    public void sendPlayerMessage(Player player, String message) {
        player.sendMessage(_pluginColor + message);
    }
    
    /**
     * Pass through for a notifying hasPermission call
     * @param player
     * @param permission
     * @return
     */
    public boolean hasPermission(Player player, String permission) {
        return hasPermission(player, permission, true);
    }
    
    /**
     * Check if a player has a given permission node, in the event they don't possibly notify them
     * @param player
     * @param permission
     * @param notify
     * @return
     */
    public boolean hasPermission(Player player, String permission, boolean notify) {
        if (player.hasPermission(permission)) {
            return true;
        } else {
            if (notify) {
                sendPlayerMessage(player, "You are missing the " + ChatColor.YELLOW + permission + _pluginColor + " permission.");
            }
            return false;
        }
    }
}
