package cz.artin.hackers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class FilipAxe extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(ZireaelSword.class.getName());

    FilipAxe(JavaPlugin plugin) {
        LOGGER.finer("FilipAxe");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.GOLDEN_AXE, FilipAxe.class.getName());
    }

    public void interact(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (Effect.removeMana(player, Mana.Colour.RED, 5)) {
                if (Effect.removeMana(player, Mana.Colour.BLACK, 5)) {
                    //  Effect.launchFireball(player);
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player.setFireTicks(100);
                    }
                }
                if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
                    if (Effect.removeMana(player, Mana.Colour.RED, 1)) {
                        Effect.createFireLine(player);
                    }
                }
            }

        }
    }
}

