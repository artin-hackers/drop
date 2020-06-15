package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

public class Item {
    private final static Logger LOGGER = Logger.getLogger(Item.class.getName());

    public static boolean equip(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) || args.length != 1) {
            LOGGER.warning("Drop.equip: Invalid command");
            return false;
        }
        Player player = (Player) sender;
        String itemName = args[0];
        ItemStack item;
        if (itemName.equalsIgnoreCase("MagicWand")) {
            item = new ItemStack(Material.STICK, 1);
        } else {
            LOGGER.warning("Drop.equip: Unknown item");
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            LOGGER.warning("Drop.equip: Meta data are null");
            return false;
        }
        meta.setDisplayName(itemName);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        return true;
    }
}
