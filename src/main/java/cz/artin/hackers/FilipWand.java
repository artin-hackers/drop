package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class FilipWand extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(FilipWand.class.getName());

    FilipWand(JavaPlugin plugin) {
        LOGGER.finer("FilipWand");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.STICK, FilipWand.class.getName());
    }

    public void interact(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (Effect.removeMana(player, Mana.Colour.GREEN, 1)) {
                Effect.creategauge(player);
            } else {
                player.sendMessage("Not enough mana");
            }
        }
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            Effect.createHole(player);
            Effect.addMana(player, Mana.Colour.GREEN, 1);
        }
    }
}
