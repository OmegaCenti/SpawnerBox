package net.betterverse.monsterbox;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MonsterBoxCommands
  implements CommandExecutor
{
  MonsterBox plugin;

  public MonsterBoxCommands(MonsterBox plugin)
  {
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if ((sender instanceof Player)) {
      Player player = (Player)sender;
      if (commandLabel.equalsIgnoreCase("mbox")) {
        if (args.length > 1) {
          if ((args[0].trim().equalsIgnoreCase("set")) && (player.getTargetBlock(this.plugin.transparentBlocks, 40).getTypeId() == 52)) {
            if (this.plugin.hasPermissions(player, "monsterbox.set")) {
              if (this.plugin.hasPermissions(player, "monsterbox.spawn." + args[1].toLowerCase())) {
                if ((this.plugin.useiconomy)) {
                  if (this.plugin.hasPermissions(player, "monsterbox.free")) {
                    if (this.plugin.setSpawner(player.getTargetBlock(this.plugin.transparentBlocks, 40), args[1])) {
                      player.sendMessage(ChatColor.DARK_GREEN + "Poof! That mob spawner is now a " + args[1].toLowerCase() + " spawner.");
                      return true;
                    }
                    player.sendMessage(ChatColor.RED + "Invalid mob type.");
                  }
                  else if (MonsterBox.economy.hasAccount(player.getName())) {
                    if (MonsterBox.economy.has(player.getName(),this.plugin.getMobPrice(args[1]))) {
                      if (this.plugin.setSpawner(player.getTargetBlock(this.plugin.transparentBlocks, 40), args[1])) {
                        MonsterBox.economy.withdrawPlayer(player.getName(),this.plugin.getMobPrice(args[1]));
                        player.sendMessage(ChatColor.DARK_GREEN + "Poof! That mob spawner is now a " + args[1].toLowerCase() + " spawner.");
                        return true;
                      }
                      player.sendMessage(ChatColor.RED + "Invalid mob type.");
                    }
                    else {
                      player.sendMessage(ChatColor.RED + "You need " + MonsterBox.economy.format(this.plugin.getMobPrice(args[1])) + " to set the type of monster spawner!");
                    }
                  } else {
                    player.sendMessage(ChatColor.RED + "You need a bank account and " + MonsterBox.economy.format(this.plugin.getMobPrice(args[1])) + " to set the type of monster spawner!");
                  }
                } else {
                  if (this.plugin.setSpawner(player.getTargetBlock(this.plugin.transparentBlocks, 40), args[1])) {
                    player.sendMessage(ChatColor.DARK_GREEN + "Poof! That mob spawner is now a " + args[1].toLowerCase() + " spawner.");
                    return true;
                  }
                  player.sendMessage(ChatColor.RED + "Invalid mob type.");
                }
              }
              else {
                player.sendMessage(ChatColor.RED + "You don't have permission to create a " + args[1].toLowerCase() + " spawner.");
                return true;
              }
            }
            else player.sendMessage(ChatColor.RED + "You don't have permission to change spawner types!");
          }
          else
            return false;
        }
        else if (args.length == 1) {
          if ((args[0].trim().equalsIgnoreCase("set")) && (player.getTargetBlock(this.plugin.transparentBlocks, 40).getTypeId() == 52)) {
            if (this.plugin.usespout != null) {
              SpoutPlayer splayer = SpoutManager.getPlayer(player);
              if (splayer.isSpoutCraftEnabled()) {
                splayer.closeActiveWindow();
                CreatureSpawner theSpawner = (CreatureSpawner)player.getTargetBlock(this.plugin.transparentBlocks, 40).getState();
                String monster = theSpawner.getCreatureTypeId().toLowerCase();
                this.plugin.ss.createMonsterGUI("This is currently a " + monster + " spawner.", !this.plugin.hasPermissions(splayer, "monsterbox.free"), splayer);
                return true;
              }
            } else {
              player.sendMessage(ChatColor.GREEN + "To set the Spawner type: /mbox set <mobname>");
              CreatureType[] values = CreatureType.values();
              String mobs = "";
              for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                  mobs = mobs + ", ";
                }
                mobs = mobs + values[i].getName();
              }
              player.sendMessage(ChatColor.GREEN + "Valid mob types: " + mobs);
              return true;
            }
          } else {
            if (args[0].equalsIgnoreCase("get")) {
              if (this.plugin.hasPermissions(player, "monsterbox.view")) {
                Block targetblock = player.getTargetBlock(this.plugin.transparentBlocks, 40);
                if (targetblock.getType() == Material.MOB_SPAWNER) {
                  try {
                    CreatureSpawner theSpawner = (CreatureSpawner)targetblock.getState();
                    String monster = theSpawner.getCreatureTypeId().toLowerCase();
                    player.sendMessage(ChatColor.GREEN + "That is a " + ChatColor.RED + monster + ChatColor.GREEN + " spawner.");
                    return true;
                  } catch (Exception e) {
                    return false;
                  }
                }
                player.sendMessage(ChatColor.RED + "You must target a MobSpawner first!");
                return true;
              }

              player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
              return true;
            }

            player.sendMessage(ChatColor.GREEN + "To get the Spawner type: /mbox get");
          }
        } else {
          player.sendMessage(ChatColor.GREEN + "To set the Spawner type: /mbox set <mobname>");
          CreatureType[] values = CreatureType.values();
          String mobs = "";
          for (int i = 0; i < values.length; i++) {
            if (i > 0) {
              mobs = mobs + ", ";
            }
            mobs = mobs + values[i].getName();
          }
          player.sendMessage(ChatColor.GREEN + "Valid mob types: " + mobs);
        }
      }
    }

    return false;
  }
}