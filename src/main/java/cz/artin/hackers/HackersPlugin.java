package cz.artin.hackers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class HackersPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Hackers plugin se nacita...");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return false;
    }
}
