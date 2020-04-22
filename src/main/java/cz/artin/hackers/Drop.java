package cz.artin.hackers;

import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.concurrent.ThreadLocalRandom;

public class Drop extends JavaPlugin implements Listener {
    private static final int DEFAULT_DUMMY_COUNT = 10;
    private static final int DEFAULT_DUMMY_RADIUS = 10;

    private Location PORTAL_EXIT = null;

    @Override
    public void onEnable() {
        getLogger().info("Loading DROP plugin...");
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("...plugin successfully loaded.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("setModeDeveloper")) {
            return setModeDeveloper(sender);
        }
        else if (label.equalsIgnoreCase("setModeNormal")) {
            return setModeNormal(sender);
        }
        else if (label.equalsIgnoreCase("setPortalExit")) {
            return setPortalExit(sender);
        }
        else if (label.equalsIgnoreCase("spawnChicken")) {
            return spawnChicken(sender);
        }
        else if (label.equalsIgnoreCase("spawnDummies")) {
            return spawnDummies(sender, args);
        }
        else if (label.equalsIgnoreCase("teleport")) {
            return teleport(sender);
        }
        else if (label.equalsIgnoreCase("sethometown")) {
            getLogger().info("sethometown");
            return setHometown(sender);
        }
        else if (label.equalsIgnoreCase("createFireballAxe")) {
            getLogger().info("createFireballAxe");
            return createFireballAxe(sender);
        }
        else if (label.equalsIgnoreCase("buildObelisk")) {
            getLogger().info("buildObelisk()");
            return buildObelisk(sender);
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        getLogger().info("A new player, " + event.getPlayer().getName() + ", just joined the fray.");
        event.getPlayer().setGameMode(GameMode.CREATIVE);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        getLogger().info("onInteract() - Init");
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (event.getClickedBlock().getType().equals(Material.DIAMOND_BLOCK)) {
                if (PORTAL_EXIT != null) {
                    event.getPlayer().teleport(PORTAL_EXIT);
                } else {
                    event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());

                    //if (itemInMainHand.getItemMeta().getDisplayName().equals("createFireballAxe")) {
                    //   event.getPlayer().launchProjectile(Fireball.class);
                    //}


                }
            }
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
            if (itemInMainHand != null && itemInMainHand.getItemMeta() != null) {
                if (itemInMainHand.getItemMeta().getDisplayName().equals("createFireballAxe")) {
                    event.getPlayer().launchProjectile(Fireball.class);
                }
            }
        }
    }

    private int getValueInt(String[] args, int index, int default_value) {
            if (index < 0 || index >= args.length) {
                return default_value;
            } else {
                return Integer.valueOf(args[index]);
            }
        }


    private boolean setModeDeveloper(CommandSender sender) {
        if (sender instanceof Player) {
            ((Player) sender).setGameMode(GameMode.CREATIVE);
            ((Player) sender).getWorld().setTime(0);
            ((Player) sender).getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
        return true;
    }

    private boolean setHometown(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PORTAL_EXIT = player.getLocation().clone();
            Location plattformCenter = player.getLocation().clone();
            plattformCenter.add(0, -1, 0);
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    Location plattformBlockPosition = new Location(
                            player.getWorld(),
                            plattformCenter.getX() + x,
                            plattformCenter.getY(),
                            plattformCenter.getZ() + z);
                    plattformBlockPosition.getBlock().setType(Material.GLASS);
                    plattformBlockPosition.add(0, 4, 0);
                    plattformBlockPosition.getBlock().setType(Material.GREEN_WOOL);

                }
            }
            for (int y = 0; y < 3; y++) {
                Location columnBlockPosition = player.getLocation().clone();
                columnBlockPosition.add(-2, y, 0);
                columnBlockPosition.getBlock().setType(Material.EMERALD_BLOCK);
                columnBlockPosition = player.getLocation().clone();
                columnBlockPosition.add(2, y, 0);
                columnBlockPosition.getBlock().setType(Material.EMERALD_BLOCK);
            }
        }

        return true;
    }

    private boolean setModeNormal(CommandSender sender) {
        if (sender instanceof Player) {
            ((Player) sender).setGameMode(GameMode.SURVIVAL);
            ((Player) sender).getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        }
        return true;
    }

    private boolean setPortalExit(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PORTAL_EXIT = player.getLocation().clone();
        }
        return true;
    }

    private boolean spawnChicken(CommandSender sender) {

        return true;
    }


    private boolean createFireballAxe (CommandSender sender) {
        if (sender instanceof Player) {
            Player me = (Player) sender;
            ItemStack axe = new ItemStack(Material.DIAMOND_AXE, 1);
            ItemMeta meta = axe.getItemMeta();
            meta.setDisplayName("Filipovasekera");
            axe.setItemMeta(meta);
            me.getInventory().addItem(axe);

        }
        return true;
    }

    private boolean buildObelisk(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location playerLocation = player.getLocation().clone();
            Location obeliskLocation = player.getLocation().clone();
            obeliskLocation.add(5, 0, 0);
            obeliskLocation.getBlock().setType(Material.BLACK_CONCRETE);
            obeliskLocation.add(0, 1, 0);
            obeliskLocation.getBlock().setType(Material.BLACK_CONCRETE);
            obeliskLocation.add(0, 1, 0);
            obeliskLocation.getBlock().setType(Material.DIAMOND_BLOCK);
        }
        return true;
    }

    private boolean spawnDummies(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location playerLocation = player.getLocation().clone();
            int count = getValueInt(args, 0, DEFAULT_DUMMY_COUNT);
            int radius = getValueInt(args, 1, DEFAULT_DUMMY_RADIUS);
            // TODO: Raise an exception if count is negative
            // TODO: Raise an exception if radius is zero or negative
            for (int i = 0; i < count; i++) {
                int randomX = ThreadLocalRandom.current().nextInt(-radius, radius);
                int randomZ = ThreadLocalRandom.current().nextInt(-radius, radius);
                final Location dummyLocation = new Location(
                        player.getWorld(),
                        playerLocation.getX() + randomX,
                        playerLocation.getY(),
                        playerLocation.getZ() + randomZ);
                Chicken dummy = player.getWorld().spawn(dummyLocation, Chicken.class);
            }
        }
        return true;
    }

    private boolean teleport(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PORTAL_EXIT != null) {
                player.teleport(PORTAL_EXIT);
            }
            else {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
        return true;
    }
}
