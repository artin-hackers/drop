package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Item {
    private final static Logger LOGGER = Logger.getLogger(Item.class.getName());

    public Item(JavaPlugin plugin) {
        LOGGER.info("Item(): Started");
        LOGGER.info("Item(): Finished");
    }

    public static boolean equip(CommandSender sender, String[] args) {
        LOGGER.info("Item.equip(): Started");
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = new ItemStack(Material.STICK, 1);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("MagicWand");
            } else {
                LOGGER.warning("MagicWand.equip(): Meta data are null");
                return false;
            }
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
        }
        LOGGER.info("Item.equip(): Finished");
        return true;
    }
}
