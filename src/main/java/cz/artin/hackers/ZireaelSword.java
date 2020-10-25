package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ZireaelSword extends Item implements Listener {
    private final Logger LOGGER = Logger.getLogger(ZireaelSword.class.getName());

    public ZireaelSword(JavaPlugin plugin) {
        LOGGER.finer("ZireaelSword");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void equip(Player player) {
        LOGGER.finer("equip");
        equip(player, Material.DIAMOND_SWORD, ZireaelSword.class.getName());
    }

    @Override
    public void effect(Player player, Action action) {
        LOGGER.finer("effect");
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Effect.blinkForward(player, 10);
        }
    }
}

