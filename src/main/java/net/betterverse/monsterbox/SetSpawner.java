package net.betterverse.monsterbox;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

public class SetSpawner
        implements Runnable {
    CreatureSpawner ts;
    EntityType ct;

    public SetSpawner(CreatureSpawner theSpawner, EntityType ct) {
        this.ts = theSpawner;
        this.ct = ct;
    }

    public void run() {
        this.ts.setSpawnedType(ct);
    }
}