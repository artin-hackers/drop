package cz.artin.hackers;

import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public class Drop extends JavaPlugin implements Listener {
    private static final int DEFAULT_DUMMY_COUNT = 10;
    private static final int DEFAULT_DUMMY_RADIUS = 10;

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
        else if (label.equalsIgnoreCase("spawnDummies")) {
            return spawnDummies(sender, args);
        }
        else if (label.equalsIgnoreCase("spawnChicken")) {
            return spawnChicken(sender);
        }
        else if (label.equalsIgnoreCase("spawnRabbit")) {
            return spawnRabbit(sender);
        }
        else if (label.equalsIgnoreCase("spawnWolf")) {
            return spawnWolf(sender);
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        getLogger().info("A new player, " + event.getPlayer().getName() + ", just joined the fray.");
        event.getPlayer().setGameMode(GameMode.CREATIVE);
    }

    private boolean setModeDeveloper(CommandSender sender) {
        if (sender instanceof Player) {
            ((Player) sender).setGameMode(GameMode.CREATIVE);
            ((Player) sender).getWorld().setTime(0);
            ((Player) sender).getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
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

    private boolean spawnChicken(CommandSender sender) {
        // TODO
        return true;
    }

    private boolean spawnDummies(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location playerLocation = player.getLocation().clone();
            int count = DEFAULT_DUMMY_COUNT;
            int radius = DEFAULT_DUMMY_RADIUS;

            if (args.length == 1) {
                count = Integer.valueOf(args[0]);
            }
            else if (args.length == 2) {
                count = Integer.valueOf(args[0]);
                radius = Integer.valueOf(args[1]);
            }

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

    private boolean spawnRabbit(CommandSender sender) {
        // TODO
        return true;
    }

    private boolean spawnWolf(CommandSender sender) {
        // TODO
        return true;
    }
}
