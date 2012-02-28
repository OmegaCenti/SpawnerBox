package net.betterverse.monsterbox;

import org.bukkit.entity.EntityType;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutStuff {
    MonsterBox plugin;

    public SpoutStuff(MonsterBox plugin) {
        this.plugin = plugin;
    }

    public void createMonsterGUI(String title, boolean showprices, SpoutPlayer splayer) {
        if (this.plugin.usespout != null) {
            GenericPopup monsters = new GenericPopup();
            EntityType[] mobs = EntityType.values();
            int x = 5;
            int y = 20;
            GenericLabel label = new GenericLabel(title);
            label.setWidth(200).setHeight(20);
            label.setTextColor(new Color(0, 200, 0));
            label.setAlign(WidgetAnchor.TOP_CENTER).setAnchor(WidgetAnchor.TOP_CENTER);
            label.shiftYPos(5);
            monsters.attachWidget(this.plugin, label);
            for (EntityType mob : mobs) {
                String price = "";
                if ((showprices) && (this.plugin.useiconomy)) {
                    price = "(" + MonsterBox.economy.format(this.plugin.getMobPrice(mob.getName())) + ") ";
                }
                GenericButton tbutton = new GenericButton(price + mob.getName());
                tbutton.setX(x).setY(y);
                tbutton.setWidth(this.plugin.buttonwidth).setHeight(20);
                monsters.attachWidget(this.plugin, tbutton);
                y += 30;
                if (y > 180) {
                    y = 20;
                    x += this.plugin.buttonwidth + 5;
                }
            }
            GenericButton tbutton = new GenericButton("Close");
            tbutton.setX(200).setY(210);
            tbutton.setWidth(80).setHeight(20);
            monsters.attachWidget(this.plugin, tbutton);
            splayer.getMainScreen().attachPopupScreen(monsters);
        }
    }
}