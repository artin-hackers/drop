package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Trident extends Item implements Listener {
    Trident(JavaPlugin plugin) {
//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.TRIDENT, Trident.class.getName());
    }

    public void interact(PlayerInteractEvent event) {
    }
}
