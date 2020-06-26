package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

// TODO: Implements Listener
public abstract class Item implements Drop.ItemEquip {
    private final Logger LOGGER = Logger.getLogger(Item.class.getName());

    public abstract void effect(Player player, Action action);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (isItemInMainHand(event, this.getClass().getName())) {
            effect(event.getPlayer(), event.getAction());
        }
    }

    public void equip(Player player, Material material, String displayName) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            LOGGER.warning("Cannot get item meta data");
            return;
        }
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }

    private boolean isItemInMainHand(PlayerInteractEvent event, String displayName) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() != null) {
            return itemInMainHand.getItemMeta().getDisplayName().equals(displayName);
        } else {
            return false;
        }
    }
}
