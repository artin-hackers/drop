package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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

public class ItemMagicWand implements Listener {
    private final static Logger LOGGER = Logger.getLogger(ItemMagicWand.class.getName());

    public ItemMagicWand(JavaPlugin plugin) {
        LOGGER.info("MagicWand(): Started");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        LOGGER.info("MagicWand(): Finished");
    }

    public static boolean equip(CommandSender sender) {
        LOGGER.info("MagicWand.equip(): Started");
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
        LOGGER.info("MagicWand.equip(): Finished");
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        LOGGER.info("MagicWand.onInteract(): Started");
        if (event.getAction().equals(Action.LEFT_CLICK_AIR)
                || event.getAction().equals(Action.LEFT_CLICK_BLOCK )) {
            ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
            ItemMeta meta = itemInMainHand.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                if (meta.getDisplayName().equals("MagicWand")) {
                    event.getPlayer().launchProjectile(Fireball.class);
                }
            } else {
                LOGGER.warning("MagicWand.onInteract(): Cannot get display name");
            }
        }
        LOGGER.info("MagicWand.onInteract(): Finished");
    }
}
