package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class DebugStick extends Item implements Listener {
    private final Logger LOGGER = Logger.getLogger(DebugStick.class.getName());
    private int active_effect = 0;

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
            LOGGER.fine("effect: apply effect " + active_effect);
            switch (active_effect) {
                case 0:
                    Effect.launchFireball(player);
                    break;
                case 1:
                    Effect.blinkForward(player, 10);
                    break;
                case 2:
                    Effect.dealDamage(player, 2);
                    break;
                default:
                    LOGGER.warning("Invalid effect ID");
            }
        } else if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            active_effect = (active_effect + 1) % 3;
            LOGGER.fine("effect: effect changed to " + active_effect);
            player.sendMessage("Active effect " + active_effect);
        }
    }
}
