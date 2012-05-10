package net.betterverse.spawnerbox;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpawnerEditor {
    private final List<EntityType> mobs = new ArrayList<EntityType>();
    private final Player player;
    private Block editing;
    private int currentMob;

    public SpawnerEditor(Player player) {
        this.player = player;

        calculatePossibleMobs();
    }

    public boolean canEdit() {
        return mobs.size() > 0;
    }

    public void onInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
        case LEFT_CLICK_BLOCK:
            // Don't allow player to cycle through mobs until it has right-clicked a block
            if (editing == null) {
                player.sendMessage(ChatColor.GREEN + "Right-click a mob spawner to change the mob that it will spawn.");
                return;
            }
            if (++currentMob > mobs.size() - 1) {
                currentMob = 0;
            }
            // Cycle through possible mobs based on the player's permissions
            player.sendMessage(ChatColor.GREEN + "Changed mob to " + ChatColor.YELLOW
                    + mobs.get(currentMob).name().toLowerCase().replace('_', ' ') + ChatColor.GREEN
                    + ". Right-click to confirm.");
            break;
        case RIGHT_CLICK_BLOCK:
            if (event.getClickedBlock().getType() == Material.MOB_SPAWNER) {
                if (editing == null) {
                    // Set the edited block
                    editing = event.getClickedBlock();
                    player.sendMessage(ChatColor.GREEN + "Mob spawner was set to block at location: "
                            + ChatColor.YELLOW + editing.getX() + ChatColor.GREEN + ", " + ChatColor.YELLOW
                            + editing.getY() + ChatColor.GREEN + ", " + ChatColor.YELLOW + editing.getZ()
                            + ChatColor.GREEN + " in world " + ChatColor.YELLOW + editing.getWorld().getName()
                            + ChatColor.GREEN + ". Left-click the mob spawner to cycle through mobs.");
                } else {
                    if (editing.equals(event.getClickedBlock())) {
                        EntityType mob = mobs.get(currentMob);
                        CreatureSpawner spawned = (CreatureSpawner) event.getClickedBlock().getState();
                        spawned.setSpawnedType(mob);
                        // Update the client
                        spawned.update();

                        player.sendMessage(ChatColor.GREEN + "This mob spawner now will spawn " + ChatColor.YELLOW
                                + mob.name().toLowerCase().replace('_', ' ') + ChatColor.GREEN + ". Enter "
                                + ChatColor.YELLOW + "/sb set" + ChatColor.GREEN + " again to exit the editor.");
                    } else {
                        // The player is already editing a different block
                        player.sendMessage(ChatColor.RED + "That is not the correct block!");
                    }
                }
            }
        }
    }

    private void calculatePossibleMobs() {
        for (EntityType entity : EntityType.values()) {
            // Only check LivingEntities, don't include players
            if (!entity.isAlive() || entity.getEntityClass().equals(Player.class)) {
                continue;
            }
            if (player.hasPermission("spawnerbox.set." + entity.name().toLowerCase().replace("_", ""))) {
                mobs.add(entity);
            }
        }
    }
}