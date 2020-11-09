package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

public class FilipAxe extends Item implements Listener {
    FilipAxe(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.GOLDEN_AXE, FilipAxe.class.getName());
    }

    public void interact(Player player, Action action) {
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Effect.launchFireball(player);

        }
    }
}


