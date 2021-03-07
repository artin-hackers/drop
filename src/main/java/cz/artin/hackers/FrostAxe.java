package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class FrostAxe extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(ZireaelSword.class.getName());

    FrostAxe(JavaPlugin plugin) {
        LOGGER.finer("FrostAxe");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.DIAMOND_AXE, FrostAxe.class.getName());
    }
    
    private boolean setGroundFreeze(Location location, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                final Location currentLocation = new Location(
                        location.getWorld(),
                        location.getX() + x,
                        location.getY(),
                        location.getZ() + z);
                currentLocation.getBlock().setType(Material.ICE);
            }
        }
        return true;
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            LOGGER.info("Snowball hit something");
            setGroundFreeze(event.getEntity().getLocation(), 2);
        }
    }

    public void interact(Player player, Action action) {
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (Effect.removeMana(player, Mana.Colour.GREEN, 1)) {
                Effect.launchSnowball(player);
            }
        }
    }
}
