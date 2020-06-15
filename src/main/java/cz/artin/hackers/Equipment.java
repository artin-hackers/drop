package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Equipment implements Listener {
    private final static Logger LOGGER = Logger.getLogger(Equipment.class.getName());

    public Equipment(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static boolean equip(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) || args.length != 1) {
            LOGGER.warning("Invalid use of equip command");
            return false;
        }
        Player player = (Player) sender;
        String itemName = args[0];
        ItemStack item;
        if (itemName.equalsIgnoreCase("MagicWand")) {
            item = new ItemStack(Material.STICK, 1);
        } else {
            LOGGER.warning("Unknown item requested");
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            LOGGER.warning("Cannot get item meta data");
            return false;
        }
        meta.setDisplayName(itemName);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() != null) {
            String itemDisplayName = itemInMainHand.getItemMeta().getDisplayName();
            if (itemDisplayName.equals("MagicWand")) {
                EquipmentMagicWand.use(event);
            }
        }
    }
}
