package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SwordOfTheDamned extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(ZireaelSword.class.getName());

    SwordOfTheDamned(JavaPlugin plugin) {
        LOGGER.finer("SwordOfTheDamned");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.DIAMOND_SHOVEL, SwordOfTheDamned.class.getName());
    }

    public void interact(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (Effect.removeMana(player, Mana.Colour.BLACK, 1)) {
                Effect.spawnZombies(player);
            } else {
                player.sendMessage("Not enough mana");
            }
        }
    }
}
