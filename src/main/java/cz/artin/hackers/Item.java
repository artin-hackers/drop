package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

public class Item {
    private final Logger LOGGER = Logger.getLogger(Item.class.getName());

    public void equipItem(Player player, Material material, String displayName) {
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
}
