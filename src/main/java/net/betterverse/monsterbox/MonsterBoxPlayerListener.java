package net.betterverse.monsterbox;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MonsterBoxPlayerListener implements Listener
{
  MonsterBox plugin;

  public MonsterBoxPlayerListener(MonsterBox plugin)
  {
    this.plugin = plugin;
  }

  public void onPlayerInteract(PlayerInteractEvent event) {
    ItemStack is = event.getPlayer().getItemInHand();
    if (is.getType() == Material.MOB_SPAWNER) {
      this.plugin.playermonsterspawner.put(event.getPlayer().getName(), new Integer(is.getDurability()));
    } else if ((this.plugin.usespout != null) && (is.getType().getId() == this.plugin.tool) && (event.getClickedBlock() != null) && (event.getClickedBlock().getTypeId() == 52)) {
      Player player = event.getPlayer();
      SpoutPlayer splayer = SpoutManager.getPlayer(player);
      if ((splayer.isSpoutCraftEnabled()) && (this.plugin.hasPermissions(player, "monsterbox.set"))) {
        CreatureSpawner theSpawner = (CreatureSpawner)event.getClickedBlock().getState();
        String monster = theSpawner.getCreatureTypeId().toLowerCase();
        splayer.closeActiveWindow();
        this.plugin.ss.createMonsterGUI("This is currently a " + monster + " spawner.", !this.plugin.hasPermissions(splayer, "monsterbox.free"), splayer);
      }
    }
  }

  public void onItemHeldChange(PlayerItemHeldEvent event)
  {
    ItemStack is = event.getPlayer().getInventory().getItem(event.getNewSlot());
    if (is.getType() == Material.MOB_SPAWNER)
      event.getPlayer().sendMessage(ChatColor.GOLD + "You are now holding a " + ChatColor.DARK_RED + (String)this.plugin.bl.intmobs.get(new Integer(is.getDurability())) + ChatColor.GOLD + " spawner.");
  }
}