package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

public class MagicWand extends Item implements Listener {
    // TODO: Move to Item class
    public MagicWand(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void equip(Player player) {
        add(player, Material.STICK, MagicWand.class.getName());
    }

    @Override
    public void interact(Player player, Action action) {
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            Effect.launchFireball(player);
        }
    }
}
