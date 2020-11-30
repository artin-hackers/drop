package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class bow extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(bow.class.getName());

    bow (JavaPlugin plugin) {
        LOGGER.finer("bow");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public void add(Player player) {
        add(player, Material.BOW, bow.class.getName());
    }

