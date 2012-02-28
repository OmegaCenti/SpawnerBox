package net.betterverse.monsterbox;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spout.Spout;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MonsterBox extends JavaPlugin {
    private final HashMap<Player, Boolean> debugees = new HashMap();
    private final ConcurrentHashMap<String, Double> mobprice = new ConcurrentHashMap();
    public MonsterBoxBlockListener bl;
    boolean useiconomy = false;
    public double iconomyprice = 0.0D;
    public Spout usespout = null;
    public boolean separateprices = false;
    public int tool = Material.GOLD_SWORD.getId();
    public int buttonwidth = 100;
    public String version = "0.4";
    public SpoutStuff ss = null;
    public HashSet<Byte> transparentBlocks = new HashSet();
    private ConcurrentHashMap<String, String> mobcase = new ConcurrentHashMap();
    ConcurrentHashMap<String, Integer> playermonsterspawner = new ConcurrentHashMap();
    public static Economy economy = null;

    public MonsterBox() {
        loadconfig();
        loadprices();

        this.transparentBlocks.add((byte) 0);
        this.transparentBlocks.add((byte) 8);
        this.transparentBlocks.add((byte) 9);
        this.transparentBlocks.add((byte) 20);
        this.transparentBlocks.add((byte) 30);
        this.transparentBlocks.add((byte) 65);
        this.transparentBlocks.add((byte) 66);
        this.transparentBlocks.add((byte) 78);
        this.transparentBlocks.add((byte) 83);
        this.transparentBlocks.add((byte) 101);
        this.transparentBlocks.add((byte) 102);
        this.transparentBlocks.add((byte) 106);
    }

    private Boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    private void loadprices() {
        File folder = new File("plugins/MonsterBox");

        File configFile = new File("plugins/MonsterBox/prices.ini");

        if (configFile.exists()) {
            try {
                this.mobprice.clear();
                Properties theprices = new Properties();
                theprices.load(new FileInputStream(configFile));
                Iterator iprices = theprices.entrySet().iterator();
                while (iprices.hasNext()) {
                    Map.Entry price = (Map.Entry) iprices.next();
                    try {
                        this.mobprice.put(price.getKey().toString().toLowerCase(), new Double(price.getValue().toString()));
                    } catch (NumberFormatException ex) {
                        System.out.println("[MonsterBox] Unable to parse the value for " + price.getKey().toString());
                    }
                }
            } catch (IOException localIOException) {
            }
            if (this.mobprice.size() < EntityType.values().length) {
                System.out.println("[MonsterBox] - New mobs found! Updating prices.ini");
                createprices();
            }
        } else {
            System.out.println("[MonsterBox] Price file not found");
            folder.mkdir();

            System.out.println("[MonsterBox] - creating file prices.ini");
            createprices();
        }
    }

    private void createprices() {
        try {
            BufferedWriter outChannel = new BufferedWriter(new FileWriter("plugins/MonsterBox/prices.ini"));
            outChannel.write("#This config file contains all the separate prices for all the mobs\n# if the option separateprices is true\n\n\n");

            EntityType[] mobs = EntityType.values();
            for (EntityType mob : mobs) {
                outChannel.write(mob.getName() + " = " + String.valueOf(getMobPrice(mob.getName())) + "\n");
            }
            outChannel.close();
        } catch (Exception e) {
            System.out.println("[MonsterBox] - file creation failed, using defaults.");
        }
    }

    public void onEnable() {
        setupEconomy();
        setupSpout();
        setupMobCase();

        PluginManager pm = getServer().getPluginManager();
        this.bl = new MonsterBoxBlockListener(this);
        MonsterBoxPlayerListener pl = new MonsterBoxPlayerListener(this);
        pm.registerEvents(pl, this);
        pm.registerEvents(bl, this);
        if (this.usespout != null) {
            pm.registerEvents(new MonsterBoxScreenListener(this), this);
            this.ss = new SpoutStuff(this);
        }
        MonsterBoxCommands commandL = new MonsterBoxCommands(this);
        PluginCommand batchcommand = getCommand("mbox");
        batchcommand.setExecutor(commandL);

        PluginDescriptionFile pdfFile = getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
        System.out.println("MonsterBox disabled!");
    }

    public boolean isDebugging(Player player) {
        if (this.debugees.containsKey(player)) {
            return ((Boolean) this.debugees.get(player)).booleanValue();
        }
        return false;
    }

    public void setDebugging(Player player, boolean value) {
        this.debugees.put(player, Boolean.valueOf(value));
    }

    private void setupSpout() {
        Plugin p = getServer().getPluginManager().getPlugin("Spout");
        if (p == null) {
            this.usespout = null;
            System.out.println("[MonsterBox] Spout not detected. Disabling spout support.");
        } else {
            this.usespout = ((Spout) p);
            System.out.println("[MonsterBox] Spout detected. Spout support enabled.");
        }
    }

    public boolean hasPermissions(Player player, String node) {
        return player.hasPermission(node);
    }

    private void loadconfig() {
        File folder = new File("plugins/MonsterBox");

        File configFile = new File("plugins/MonsterBox/settings.ini");

        if (configFile.exists()) {
            try {
                Properties themapSettings = new Properties();
                themapSettings.load(new FileInputStream(configFile));

                String iconomy = themapSettings.getProperty("useEconomy", "false");
                String price = themapSettings.getProperty("price", "0.0");
                String sprices = themapSettings.getProperty("separateprices", "false");
                String swidth = themapSettings.getProperty("buttonwidth", "100");
                String stool = themapSettings.getProperty("changetool", String.valueOf(Material.GOLD_SWORD.getId()));

                String theversion = themapSettings.getProperty("version", "0.1");

                this.useiconomy = stringToBool(iconomy);
                this.separateprices = stringToBool(sprices);
                try {
                    this.tool = Integer.parseInt(stool.trim());
                } catch (Exception localException) {
                }
                try {
                    this.buttonwidth = Integer.parseInt(swidth.trim());
                } catch (Exception localException1) {
                }
                try {
                    this.iconomyprice = Double.parseDouble(price.trim());
                } catch (Exception localException2) {
                }
                double dbversion = 0.1D;
                try {
                    dbversion = Double.parseDouble(theversion.trim());
                } catch (Exception localException3) {
                }
                if (dbversion >= 0.4D) return;
                if (dbversion == 0.1D) {
                    String sconomy = themapSettings.getProperty("useiConomy", "false");
                    this.useiconomy = stringToBool(sconomy);
                }
                updateIni();
            } catch (IOException localIOException) {
            }
        } else {
            System.out.println("[MonsterBox] Configuration file not found");

            System.out.println("[MonsterBox] + creating folder plugins/MonsterBox");
            folder.mkdir();

            System.out.println("[MonsterBox] - creating file settings.ini");
            updateIni();
        }
    }

    private void updateIni() {
        try {
            BufferedWriter outChannel = new BufferedWriter(new FileWriter("plugins/MonsterBox/settings.ini"));
            outChannel.write("#This is the main MonsterBos config file\n#\n# useiConomy: Charge to change monster spawner type using your economy system\nuseEconomy = " +
                    this.useiconomy + "\n" +
                    "# price: The price to change monster spawner type\n" +
                    "price = " + this.iconomyprice + "\n\n" +
                    "# separateprices: If you want separate prices for all the different types of mobs\n" +
                    "# set this to true.\n" +
                    "separateprices = " + this.separateprices + "\n" +
                    "# changetool is the tool that opens up the spout gui for changing the monster spawner.\n" +
                    "changetool = " + this.tool + "\n" +
                    "# buttonwidth changes the width of the buttons in the spoutcraft gui, just in case the\n" +
                    "# text doesn't fit for you.\n" +
                    "buttonwidth = " + this.buttonwidth + "\n\n" +
                    "#Do not change anything below this line unless you know what you are doing!\n" +
                    "version = " + this.version);
            outChannel.close();
        } catch (Exception e) {
            System.out.println("[MonsterBox] - file creation failed, using defaults.");
        }
    }

    private synchronized boolean stringToBool(String thebool) {
        boolean result;
        if ((thebool.trim().equalsIgnoreCase("true")) || (thebool.trim().equalsIgnoreCase("yes")))
            result = true;
        else {
            result = false;
        }
        return result;
    }

    boolean setSpawner(Block targetBlock, String type) {
        try {
            CreatureSpawner theSpawner = (CreatureSpawner) targetBlock.getState();
            if (this.mobcase.containsKey(type.toLowerCase().trim()))
                type = (String) this.mobcase.get(type.toLowerCase().trim());
            else {
                type = capitalCase(type);
            }
            EntityType ct = EntityType.fromName(type);
            if (ct == null) {
                return false;
            }
            theSpawner.setSpawnedType(ct);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    String capitalCase(String s) {
        return s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
    }

    public double getMobPrice(String name) {
        if ((this.separateprices) && (this.mobprice.containsKey(name.toLowerCase()))) {
            return (Double) this.mobprice.get(name.toLowerCase());
        }
        return this.iconomyprice;
    }

    private void setupMobCase() {
        EntityType[] mobs = EntityType.values();
        for (EntityType mob : mobs) {
            String mobname = mob.name().trim();
            this.mobcase.put(mobname.toLowerCase(), mobname);
        }
    }
}