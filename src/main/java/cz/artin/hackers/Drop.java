package cz.artin.hackers;

import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
        if (label.equalsIgnoreCase("setDeveloperMode")) {
            return setDeveloperMode(sender);
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

    private boolean setDeveloperMode(CommandSender sender) {
        if (sender instanceof Player) {
            ((Player) sender).setGameMode(GameMode.CREATIVE);
            ((Player) sender).getWorld().setTime(0);
            ((Player) sender).getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
        return true;
    }

    private boolean spawnChicken(CommandSender sender) {
        // TODO
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
