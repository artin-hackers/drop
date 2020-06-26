package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

public class ZireaelSword extends Item implements Listener {
    // TODO: Move to Item class
    public ZireaelSword(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void equip(Player player) {
        equip(player, Material.DIAMOND_SWORD, ZireaelSword.class.getName());
    }

    @Override
    public void effect(Player player, Action action) {
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Effect.blinkForward(player, 10);
        }
    }
}
