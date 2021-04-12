package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ZireaelSword extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(ZireaelSword.class.getName());

    ZireaelSword(JavaPlugin plugin) {
        LOGGER.finer("ZireaelSword");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.DIAMOND_SWORD, ZireaelSword.class.getName());
    }

    public void interact(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (Effect.removeMana(player, Mana.Colour.BLUE, 1)) {
                Effect.blinkForward(player, 10);
            } else {
                player.sendMessage("Not enough mana");
            }
        }
    }
}
