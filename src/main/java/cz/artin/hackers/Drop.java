package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Drop extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Loading DROP plugin...");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("spawnChicken")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location location = player.getLocation();
                location.add(5, 0, 0);
                Chicken chicken = player.getWorld().spawn(location, Chicken.class);
                return true;
            }
            return false;
        }

        return false;
    }
}
