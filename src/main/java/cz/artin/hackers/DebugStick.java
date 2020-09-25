package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class DebugStick extends Item implements Listener {
    private final Logger LOGGER = Logger.getLogger(DebugStick.class.getName());

    public DebugStick(JavaPlugin plugin) {
        LOGGER.finer("DebugStick");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void equip(Player player) {
        LOGGER.finer("equip");
        equip(player, Material.DEBUG_STICK, DebugStick.class.getName());
    }

    @Override
    public void effect(Player player, Action action) {
        LOGGER.finer("effect");
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            LOGGER.info("effect: action " + action.toString());
        } else if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            LOGGER.info("effect: action " + action.toString());
        }
    }
}
