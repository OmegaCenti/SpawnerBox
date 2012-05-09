package net.betterverse.monsterbox;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MonsterBox extends JavaPlugin {

    @Override
    public void onEnable() {
        log(toString() + " enabled.");
    }

    @Override
    public void onDisable() {
        log(toString() + " disabled.");
    }

    public void log(Level level, String message) {
        Bukkit.getLogger().log(level, "[MonsterBox] " + message);
    }

    public void log(String message) {
        log(Level.INFO, message);
    }
}