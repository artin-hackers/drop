package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

// TODO: Create abstract class for constructor, equip and event handler
public class MagicWand implements Listener {
    private final Logger LOGGER = Logger.getLogger(MagicWand.class.getName());

    public MagicWand(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void equip(Player player) {
        ItemStack item = new ItemStack(Material.STICK, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            LOGGER.warning("Cannot get item meta data");
            return;
        }
        meta.setDisplayName(MagicWand.class.getName());
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() != null) {
            String itemDisplayName = itemInMainHand.getItemMeta().getDisplayName();
            if (itemDisplayName.equals(MagicWand.class.getName())) {
                if (event.getAction().equals(Action.LEFT_CLICK_AIR)
                        || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    launchFireball(event.getPlayer());
                }
            }
        }
    }

    // TODO: Create class for the effect
    private void launchFireball(Player player) {
        player.launchProjectile(Fireball.class);
    }
}
