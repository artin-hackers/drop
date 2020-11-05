package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class DebugStick extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(DebugStick.class.getName());
    private int activeEffect = 0;

    DebugStick(JavaPlugin plugin) {
        LOGGER.finer("DebugStick");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.DEBUG_STICK, DebugStick.class.getName());
    }

    @Override
    public void interact(Player player, Action action) {
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            switch (activeEffect) {
                case 0:
                    Effect.addMana(player, Mana.Colour.BLACK, 1);
                    break;
                case 1:
                    Effect.removeMana(player, Mana.Colour.BLACK, 1);
                    break;
                case 2:
                    Effect.blinkForward(player, 10);
                    break;
                case 3:
                    Effect.dealDamage(player, 2);
                    break;
                case 4:
                    Effect.launchFireball(player);
                    break;
                default:
                    LOGGER.warning("effect(): Invalid effect ID");
            }
        } else if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            activeEffect = (activeEffect + 1) % 5;
            player.sendMessage("Active effect " + activeEffect);
        }
    }
}
