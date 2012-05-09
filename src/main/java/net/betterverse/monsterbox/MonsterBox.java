package net.betterverse.monsterbox;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class MonsterBox extends JavaPlugin implements Listener {
    private final Map<String, SpawnerEditor> editing = new HashMap<String, SpawnerEditor>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be in game to use /mbox!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 1 && args[0].equals("set")) {
            if (!player.hasPermission("monsterbox.set")) {
                player.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }
            // Toggle player's editor
            if (editing.containsKey(player.getName())) {
                editing.remove(player.getName());
                player.sendMessage(ChatColor.GREEN + "Exited the spawner editor.");
            } else {
                SpawnerEditor editor = new SpawnerEditor(player);
                if (editor.canEdit()) {
                    editing.put(player.getName(), editor);
                    player.sendMessage(ChatColor.GREEN
                            + "Right-click a mob spawner to change the mob that it will spawn.");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission.");
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "Invalid command. /mbox set");
        }

        return true;
    }

    @Override
    public void onDisable() {
        log(toString() + " disabled.");
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(this, this);

        log(toString() + " enabled.");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getDescription().getName() + " v" + getDescription().getVersion()
                + " [Written by: ");
        for (int i = 0; i < getDescription().getAuthors().size(); i++) {
            builder.append(getDescription().getAuthors().get(i)
                    + (i + 1 != getDescription().getAuthors().size() ? ", " : ""));
        }
        builder.append("]");

        return builder.toString();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() && event.getBlock().getType() == Material.MOB_SPAWNER) {
            if (!event.getPlayer().hasPermission("monsterbox.break")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to break monster spawners.");
                return;
            }

            Block block = event.getBlock();
            // Store its metadata
            block.setMetadata("mob", new FixedMetadataValue(this, ((CreatureSpawner) block.getState()).getSpawnedType()
                    .name()));
            // Drop item
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.MOB_SPAWNER, 1));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled() && event.getBlock().getType() == Material.MOB_SPAWNER
                && event.getBlock().hasMetadata("mob")) {

            Block block = event.getBlock();
            // Spawner limits as defined in the configuration file
            if (getSpawnersWithinRadiusOfBlock(block) > getConfig().getInt("maximum-spawners-within-radius.spawners")) {
                event.getPlayer().sendMessage(ChatColor.RED + "This region has reached a maximum amount of spawners.");
                event.setCancelled(true);
            }

            // Set spawner's mob type based on its metadata
            ((CreatureSpawner) block.getState()).setSpawnedType(EntityType.fromName(block.getMetadata("mob").get(0)
                    .asString()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Exit editor if player quits the game
        if (editing.containsKey(event.getPlayer().getName())) {
            editing.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (editing.containsKey(player.getName())) {
            editing.get(player.getName()).onInteract(event);
        }
    }

    public void log(String message) {
        getServer().getLogger().info("[MonsterBox] " + message);
    }

    private int getSpawnersWithinRadiusOfBlock(Block center) {
        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();
        int radius = getConfig().getInt("maximum-spawners-within-radius.radius");
        int totalSpawners = 0;
        // Loop through all blocks within the radius of the center block
        for (int xx = x - radius; xx < x + radius; xx++) {
            for (int yy = y - radius; yy < y + radius; yy++) {
                for (int zz = z - radius; zz < z + radius; zz++) {
                    if (center.getWorld().getBlockAt(xx, yy, zz).getType() == Material.MOB_SPAWNER) {
                        totalSpawners++;
                    }
                }
            }
        }

        return totalSpawners;
    }
}