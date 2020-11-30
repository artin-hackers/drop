package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.Action;
import java.util.logging.Logger;

public class Bow extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(Bow.class.getName());

    Bow(JavaPlugin plugin) {
        LOGGER.finer("Bow");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.BOW, Bow.class.getName());
    }

    public void interact(Player player, Action action) {
    }
}
