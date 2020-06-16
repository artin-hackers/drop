package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

// TODO: Create abstract class for constructor, equip and event handler
public class ZireaelSword implements Listener {
    private final Logger LOGGER = Logger.getLogger(ZireaelSword.class.getName());

    public ZireaelSword(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void equip(Player player) {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            LOGGER.warning("Cannot get item meta data");
            return;
        }
        meta.setDisplayName(ZireaelSword.class.getName());
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() != null) {
            String itemDisplayName = itemInMainHand.getItemMeta().getDisplayName();
            if (itemDisplayName.equals(ZireaelSword.class.getName())) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                        || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    blinkForward(event.getPlayer());
                }
            }
        }
    }

    // TODO: Create class for the effect
    private void blinkForward(Player player) {
        List<Block> sight = player.getLineOfSight(null, 10);
        Location targetLocation = sight.get(sight.size()-1).getLocation();  // TODO: Check if location is air
        player.teleport(targetLocation);
    }
}
