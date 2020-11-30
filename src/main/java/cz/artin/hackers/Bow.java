package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Bow extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(Bow.class.getName());

    Bow(JavaPlugin plugin) {
        LOGGER.finer("Bow");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.BOW, Bow.class.getName());
    }

    private boolean setGroundFire(Location location, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                final Location currentLocation = new Location(
                        location.getWorld(),
                        location.getX() + x,
                        location.getY(),
                        location.getZ() + z);
                currentLocation.getBlock().setType(Material.FIRE);
            }
        }
        return true;
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            LOGGER.info("Arrow hit something");
            setGroundFire(event.getEntity().getLocation(), 2);
        }
    }
    public void interact(Player player, Action action) {
    }
}