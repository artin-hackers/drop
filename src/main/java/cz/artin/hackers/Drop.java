package cz.artin.hackers;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Drop extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Loading DROP plugin...");
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

    private boolean setDeveloperMode(CommandSender sender) {
        if (sender instanceof Player) {
            ((Player) sender).setGameMode(GameMode.CREATIVE);
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
