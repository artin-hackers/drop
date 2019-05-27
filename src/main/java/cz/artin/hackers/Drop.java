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

public class Drop extends JavaPlugin implements Listener {
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
        else if (label.equalsIgnoreCase("spawnChicken")) {
            return spawnChicken(sender);
        }
        else if (label.equalsIgnoreCase("spawnDummies")) {
            return spawnDummies(sender);
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

    private boolean spawnDummies(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location playerLocation = player.getLocation().clone();
            int position = 0;
            for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    final Location dummyLocation = new Location(
                            player.getWorld(),
                            playerLocation.getX() + x,
                            playerLocation.getY(),
                            playerLocation.getZ() + z);
                    if (position % 5 == 0) {
                        Chicken dummy = player.getWorld().spawn(dummyLocation, Chicken.class);
                    }
                    position++;
                }
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
