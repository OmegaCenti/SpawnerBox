package net.betterverse.monsterbox;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;

public class SetSpawner
  implements Runnable
{
  CreatureSpawner ts;
  CreatureType ct;

  public SetSpawner(CreatureSpawner theSpawner, CreatureType ct)
  {
    this.ts = theSpawner;
    this.ct = ct;
  }

  public void run()
  {
    this.ts.setCreatureType(this.ct);
  }
}