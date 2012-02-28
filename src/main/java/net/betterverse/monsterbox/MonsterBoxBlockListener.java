package net.betterverse.monsterbox;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

public class MonsterBoxBlockListener implements Listener {
    MonsterBox plugin;
    public ConcurrentHashMap<Integer, String> intmobs = new ConcurrentHashMap();
    public ConcurrentHashMap<String, Integer> stringmobs = new ConcurrentHashMap();

    public MonsterBoxBlockListener(MonsterBox plugin) {
        this.plugin = plugin;
        this.intmobs.put(0, "Pig");
        this.intmobs.put(1, "Chicken");
        this.intmobs.put(2, "Cow");
        this.intmobs.put(3, "Sheep");
        this.intmobs.put(4, "Squid");
        this.intmobs.put(5, "Creeper");
        this.intmobs.put(6, "Ghast");
        this.intmobs.put(7, "PigZombie");
        this.intmobs.put(8, "Skeleton");
        this.intmobs.put(9, "Spider");
        this.intmobs.put(10, "Zombie");
        this.intmobs.put(11, "Slime");
        this.intmobs.put(12, "Monster");
        this.intmobs.put(13, "Giant");
        this.intmobs.put(14, "Wolf");
        this.intmobs.put(15, "CaveSpider");
        this.intmobs.put(16, "Enderman");
        this.intmobs.put(17, "Silverfish");
        this.intmobs.put(90, "Pig");
        this.intmobs.put(93, "Chicken");
        this.intmobs.put(92, "Cow");
        this.intmobs.put(91, "Sheep");
        this.intmobs.put(94, "Squid");
        this.intmobs.put(50, "Creeper");
        this.intmobs.put(53, "Ghast");
        this.intmobs.put(57, "PigZombie");
        this.intmobs.put(51, "Skeleton");
        this.intmobs.put(52, "Spider");
        this.intmobs.put(54, "Zombie");
        this.intmobs.put(55, "Slime");
        this.intmobs.put(49, "Monster");
        this.intmobs.put(53, "Giant");
        this.intmobs.put(95, "Wolf");
        this.intmobs.put(59, "CaveSpider");
        this.intmobs.put(58, "Enderman");
        this.intmobs.put(60, "Silverfish");
        this.intmobs.put(63, "EnderDragon");
        this.intmobs.put(120, "Villager");
        this.intmobs.put(61, "Blaze");
        this.intmobs.put(96, "MushroomCow");
        this.intmobs.put(62, "MagmaCube");
        this.intmobs.put(97, "Snowman");
        this.stringmobs.put("Pig", 90);
        this.stringmobs.put("Chicken", 93);
        this.stringmobs.put("Cow", 92);
        this.stringmobs.put("Sheep", 91);
        this.stringmobs.put("Squid", 94);
        this.stringmobs.put("Creeper", 50);
        this.stringmobs.put("Ghast", 53);
        this.stringmobs.put("PigZombie", 57);
        this.stringmobs.put("Skeleton", 51);
        this.stringmobs.put("Spider", 52);
        this.stringmobs.put("Zombie", 54);
        this.stringmobs.put("Slime", 55);
        this.stringmobs.put("Monster", 49);
        this.stringmobs.put("Giant", 53);
        this.stringmobs.put("Wolf", 95);
        this.stringmobs.put("CaveSpider", 59);
        this.stringmobs.put("Enderman", 58);
        this.stringmobs.put("Silverfish", 60);
        this.stringmobs.put("EnderDragon", 63);
        this.stringmobs.put("Villager", 120);
        this.stringmobs.put("Blaze", 61);
        this.stringmobs.put("MushroomCow", 96);
        this.stringmobs.put("MagmaCube", 62);
        this.stringmobs.put("Snowman", 97);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if ((!event.isCancelled()) && (event.getBlock().getType() == Material.MOB_SPAWNER) &&
                (this.plugin.hasPermissions(event.getPlayer(), "monsterbox.drops"))) {
            CreatureSpawner theSpawner = (CreatureSpawner) event.getBlock().getState();
            String monster = theSpawner.getCreatureTypeName();
            event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just broke a " + ChatColor.RED + monster.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
            if (this.stringmobs.containsKey(monster)) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                        new ItemStack(Material.MOB_SPAWNER, 1, ((Integer) this.stringmobs.get(monster)).shortValue()));
            } else
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                        new ItemStack(Material.MOB_SPAWNER, 1, (short) 90));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if ((!event.isCancelled()) && (event.getBlockPlaced().getType() == Material.MOB_SPAWNER))
            if (this.plugin.hasPermissions(event.getPlayer(), "monsterbox.place")) {
                /*               String p = event.getPlayer().getName();
             String type = (String) this.intmobs.get(this.plugin.playermonsterspawner.get(event.getPlayer().getName()));
             CreatureSpawner theSpawner = (CreatureSpawner) event.getBlockPlaced().getState();
             event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You just placed a " + ChatColor.RED + type.toLowerCase() + ChatColor.DARK_GREEN + " spawner.");
             EntityType ct = EntityType.fromName(type);
             if (ct == null) {
                 return;
             }
             this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new SetSpawner(theSpawner, ct));*/
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission to place a monster spawner.");
            }
    }
}