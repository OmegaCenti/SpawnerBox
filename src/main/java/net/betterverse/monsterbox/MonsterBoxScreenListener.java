package net.betterverse.monsterbox;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MonsterBoxScreenListener implements Listener {
    MonsterBox plugin;

    public MonsterBoxScreenListener(MonsterBox plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onButtonClick(ButtonClickEvent event) {
        if (this.plugin == event.getButton().getPlugin()) {
            Button eventbutton = event.getButton();
            String completebutton = eventbutton.getText();
            String[] buttonsplit = completebutton.split(" ");
            String mobname = buttonsplit[(buttonsplit.length - 1)];
            SpoutPlayer player = event.getPlayer();
            if (mobname.equalsIgnoreCase("close")) {
                player.closeActiveWindow();
            } else if (this.plugin.hasPermissions(player, "monsterbox.set")) {
                if (this.plugin.hasPermissions(player, "monsterbox.spawn." +
                        mobname.toLowerCase())) {
                    if ((this.plugin.useiconomy)) {
                        if (this.plugin.hasPermissions(player, "monsterbox.free")) {
                            if (this.plugin.setSpawner(player.getTargetBlock(
                                    this.plugin.transparentBlocks, 40), mobname)) {
                                player.sendNotification("Mob Spawner changed!", this.plugin.capitalCase(mobname) + "s galore!", Material.MOB_SPAWNER);
                                player.closeActiveWindow();
                            } else {
                                player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
                            }
                        } else if (this.plugin.economy.hasAccount(
                                player.getName())) {
                            if (MonsterBox.economy.has(player.getName(), this.plugin.getMobPrice(mobname))) {
                                if (this.plugin.setSpawner(player.getTargetBlock(this.plugin.transparentBlocks, 40), mobname)) {
                                    MonsterBox.economy.withdrawPlayer(player.getName(), this.plugin.getMobPrice(mobname));
                                    player.sendNotification("Mob Spawner changed!", this.plugin.capitalCase(mobname) + "s galore!",
                                            Material.MOB_SPAWNER);
                                    player.closeActiveWindow();
                                } else {
                                    player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
                                }
                            } else
                                player.sendNotification("Insufficient Funds!", "You need " + MonsterBox.economy.format(this.plugin.getMobPrice(mobname)) + "!", Material.MOB_SPAWNER);
                        } else {
                            player.sendNotification("No Bank account!",
                                    "You need a bank account and " + MonsterBox.economy.format(this.plugin.getMobPrice(mobname)) + "!", Material.MOB_SPAWNER);
                        }
                    } else if (this.plugin.setSpawner(player.getTargetBlock(
                            this.plugin.transparentBlocks, 40), mobname)) {
                        player.sendNotification("Mob Spawner changed!", this.plugin.capitalCase(mobname) + "s galore!", Material.MOB_SPAWNER);
                        player.closeActiveWindow();
                    } else {
                        player.sendNotification("Mob Unavailable", "Invalid mob type.", Material.FIRE);
                    }
                } else
                    player.sendNotification("Mob Unavailable", "Permission denied.", Material.FIRE);
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to change spawner types!");
                player.closeActiveWindow();
            }
        }
    }
}