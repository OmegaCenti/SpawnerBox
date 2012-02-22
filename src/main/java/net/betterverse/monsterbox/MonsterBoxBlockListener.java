package net.betterverse.monsterbox;

import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class MonsterBoxBlockListener implements Listener
{
  MonsterBox plugin;
  public ConcurrentHashMap<Integer, String> intmobs = new ConcurrentHashMap();
  public ConcurrentHashMap<String, Integer> stringmobs = new ConcurrentHashMap();

  public MonsterBoxBlockListener(MonsterBox plugin) {
    this.plugin = plugin;
    this.intmobs.put(new Integer(0), "Pig");
    this.intmobs.put(new Integer(1), "Chicken");
    this.intmobs.put(new Integer(2), "Cow");
    this.intmobs.put(new Integer(3), "Sheep");
    this.intmobs.put(new Integer(4), "Squid");
    this.intmobs.put(new Integer(5), "Creeper");
    this.intmobs.put(new Integer(6), "Ghast");
    this.intmobs.put(new Integer(7), "PigZombie");
    this.intmobs.put(new Integer(8), "Skeleton");
    this.intmobs.put(new Integer(9), "Spider");
    this.intmobs.put(new Integer(10), "Zombie");
    this.intmobs.put(new Integer(11), "Slime");
    this.intmobs.put(new Integer(12), "Monster");
    this.intmobs.put(new Integer(13), "Giant");
    this.intmobs.put(new Integer(14), "Wolf");
    this.intmobs.put(new Integer(15), "CaveSpider");
    this.intmobs.put(new Integer(16), "Enderman");
    this.intmobs.put(new Integer(17), "Silverfish");
    this.intmobs.put(new Integer(90), "Pig");
    this.intmobs.put(new Integer(93), "Chicken");
    this.intmobs.put(new Integer(92), "Cow");
    this.intmobs.put(new Integer(91), "Sheep");
    this.intmobs.put(new Integer(94), "Squid");
    this.intmobs.put(new Integer(50), "Creeper");
    this.intmobs.put(new Integer(53), "Ghast");
    this.intmobs.put(new Integer(57), "PigZombie");
    this.intmobs.put(new Integer(51), "Skeleton");
    this.intmobs.put(new Integer(52), "Spider");
    this.intmobs.put(new Integer(54), "Zombie");
    this.intmobs.put(new Integer(55), "Slime");
    this.intmobs.put(new Integer(49), "Monster");
    this.intmobs.put(new Integer(53), "Giant");
    this.intmobs.put(new Integer(95), "Wolf");
    this.intmobs.put(new Integer(59), "CaveSpider");
    this.intmobs.put(new Integer(58), "Enderman");
    this.intmobs.put(new Integer(60), "Silverfish");
    this.intmobs.put(new Integer(63), "EnderDragon");
    this.intmobs.put(new Integer(120), "Villager");
    this.intmobs.put(new Integer(61), "Blaze");
    this.intmobs.put(new Integer(96), "MushroomCow");
    this.intmobs.put(new Integer(62), "MagmaCube");
    this.intmobs.put(new Integer(97), "Snowman");
    this.stringmobs.put("Pig", new Integer(90));
    this.stringmobs.put("Chicken", new Integer(93));
    this.stringmobs.put("Cow", new Integer(92));
    this.stringmobs.put("Sheep", new Integer(91));
    this.stringmobs.put("Squid", new Integer(94));
    this.stringmobs.put("Creeper", new Integer(50));
    this.stringmobs.put("Ghast", new Integer(53));
    this.stringmobs.put("PigZombie", new Integer(57));
    this.stringmobs.put("Skeleton", new Integer(51));
    this.stringmobs.put("Spider", new Integer(52));
    this.stringmobs.put("Zombie", new Integer(54));
    this.stringmobs.put("Slime", new Integer(55));
    this.stringmobs.put("Monster", new Integer(49));
    this.stringmobs.put("Giant", new Integer(53));
    this.stringmobs.put("Wolf", new Integer(95));
    this.stringmobs.put("CaveSpider", new Integer(59));
    this.stringmobs.put("Enderman", new Integer(58));
    this.stringmobs.put("Silverfish", new Integer(60));
    this.stringmobs.put("EnderDragon", new Integer(63));
    this.stringmobs.put("Villager", new Integer(120));
    this.stringmobs.put("Blaze", new Integer(61));
    this.stringmobs.put("MushroomCow", new Integer(96));
    this.stringmobs.put("MagmaCube", new Integer(62));
    this.stringmobs.put("Snowman", new Integer(97));
  }

  public void onBlockBreak(BlockBreakEvent event) {
    if ((!event.isCancelled()) && (event.getBlock().getType() == Material.MOB_SPAWNER) && 
      (this.plugin.hasPermissions(event.getPlayer(), "monsterbox.drops")))
      try {
        CreatureSpawner theSpawner = (CreatureSpawner)event.getBlock().getState();
        String monster = theSpawner.getCreatureTypeId();
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just broke a " + ChatColor.RED + monster.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
        if (this.stringmobs.containsKey(monster)) {
          event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), 
            new ItemStack(Material.MOB_SPAWNER, 1, ((Integer)this.stringmobs.get(monster)).shortValue()));
        }
        else
          event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), 
            new ItemStack(Material.MOB_SPAWNER, 1,(short) 90));
      }
      catch (Exception localException)
      {
      }
  }

  public void onBlockPlace(BlockPlaceEvent event)
  {
    if ((!event.isCancelled()) && (event.getBlockPlaced().getType() == Material.MOB_SPAWNER))
      if (this.plugin.hasPermissions(event.getPlayer(), "monsterbox.place")) {
        String type = (String)this.intmobs.get(this.plugin.playermonsterspawner.get(event.getPlayer().getName()));
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just placed a " + ChatColor.RED + type.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
        CreatureSpawner theSpawner = (CreatureSpawner)event.getBlockPlaced().getState();
        CreatureType ct = CreatureType.fromName(type);
        if (ct == null) {
          return;
        }
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new SetSpawner(theSpawner, ct));
      } else {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission to place a monster spawner.");
      }
  }
}