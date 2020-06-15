package cz.artin.hackers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ItemListener implements Listener {
    private final static Logger LOGGER = Logger.getLogger(ItemListener.class.getName());

    public ItemListener(JavaPlugin plugin) {
        LOGGER.info("ItemListener: Started");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        LOGGER.info("ItemListener: Finished");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        LOGGER.info("ItemListener: Started");
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() != null) {
            String itemDisplayName = itemInMainHand.getItemMeta().getDisplayName();
            if (itemDisplayName.equals("MagicWand")) {
                ItemMagicWand.effect(event);
            }
        }
        LOGGER.info("ItemListener: Finished");
    }
}
