package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ZdenekWand extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(ZdenekWand.class.getName());

    ZdenekWand(JavaPlugin plugin) {
        LOGGER.finer("ZdenekWand");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.STICK, ZdenekWand.class.getName());
    }

    @Override
    public void interact(Player player, Action action) {
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
