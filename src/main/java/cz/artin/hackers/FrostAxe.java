package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class FrostAxe extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(ZireaelSword.class.getName());

    FrostAxe(JavaPlugin plugin) {
        LOGGER.finer("FrostAxe");
//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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

//    @EventHandler
//    public void onHit(ProjectileHitEvent event) {
//        if (event.getEntity() instanceof Snowball) {
//            LOGGER.info("Snowball hit something");
//            if (event.getHitBlock() != null) {
//                setGroundFreeze(event.getHitBlock().getLocation(), 2);
//            } else if (event.getHitEntity() != null) {
//                Location hitEntityLocation = event.getHitEntity().getLocation();
//                for (int x = -1; x <= 1; x++) {
//                    for (int z = -1; z <= 1; z++) {
//                        final Location iceBlock = new Location(
//                                hitEntityLocation.getWorld(),
//                                hitEntityLocation.getX() + x,
//                                hitEntityLocation.getY() - 1,
//                                hitEntityLocation.getZ() + z);
//                        iceBlock.getBlock().setType(Material.ICE);
//                    }
//                }
//                for (int x = -1; x <= 1; x++) {
//                    for (int y = 0; y <= 2; y++) {
//                        for (int z = -1; z <= 1; z++) {
//                            if (!(x == 0 && z == 0)) {
//                                final Location iceBlock = new Location(
//                                        hitEntityLocation.getWorld(),
//                                        hitEntityLocation.getX() + x,
//                                        hitEntityLocation.getY() + y,
//                                        hitEntityLocation.getZ() + z);
//                                iceBlock.getBlock().setType(Material.ICE);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    public void interact(PlayerInteractEvent event) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() != null) {
            if (itemInMainHand.getItemMeta().getDisplayName().equals(this.getClass().getName())) {
                Action action = event.getAction();
                if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
                    if (Effect.removeMana(event.getPlayer(), Mana.Colour.GREEN, 1)) {
                        Effect.launchSnowball(event.getPlayer());

                    }
                } else if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                    Block block = event.getClickedBlock();
                    block.setType(Material.ICE);
                }
            }
        }
    }
}
