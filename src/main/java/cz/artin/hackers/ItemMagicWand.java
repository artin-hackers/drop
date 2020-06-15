package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

public class ItemMagicWand {
    private final static Logger LOGGER = Logger.getLogger(ItemMagicWand.class.getName());

    public static boolean init() {
        LOGGER.info("MagicWand.init(): Started");
        LOGGER.info("MagicWand.init(): Finished");
        return true;
    }

    public static boolean equip(CommandSender sender) {
        LOGGER.info("MagicWand.equip(): Started");
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack wand = new ItemStack(Material.STICK, 1);
            ItemMeta meta = wand.getItemMeta();
            meta.setDisplayName("MagicWand");
            wand.setItemMeta(meta);
            player.getInventory().addItem(wand);
        }
        LOGGER.info("MagicWand.equip(): Finished");
        return true;
    }
}
