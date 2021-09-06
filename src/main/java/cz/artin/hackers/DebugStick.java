package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class DebugStick extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(DebugStick.class.getName());

    DebugStick(JavaPlugin plugin) {
        LOGGER.finer("DebugStick");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.DEBUG_STICK, DebugStick.class.getName());
    }

    public void interact(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            for (int i = 0; i < 5; i++) {
                Effect.launchArrow(player);
                Effect.launchFireball(player);
                Effect.launchSnowball(player);
            }
        } else if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Effect.launchArrow(player);
        }
    }
}
