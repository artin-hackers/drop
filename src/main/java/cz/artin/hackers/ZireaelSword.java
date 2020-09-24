package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ZireaelSword extends Item implements Listener {
    // TODO: Move to Item class
    public ZireaelSword(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
//    public void equip(Player player) {
//        equip(player, Material.DIAMOND_SWORD, ZireaelSword.class.getName());
//    }
    public void equip(Player player) {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.setDisplayName(ZireaelSword.class.getName()); // TODO: Simplify name
        item.setItemMeta(meta);
        player.getInventory().addItem(item);

        ItemStack itemMana = new ItemStack(Material.BLUE_DYE, 5);
        ItemMeta metaMana = item.getItemMeta();
        if (metaMana == null) {
            return;
        }
        metaMana.setDisplayName("Mana");
        itemMana.setItemMeta(metaMana);
        player.getInventory().addItem(itemMana);


    }

    @Override
    public void effect(Player player, Action action) {
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Effect.blinkForward(player, 10);
        }
    }
}
