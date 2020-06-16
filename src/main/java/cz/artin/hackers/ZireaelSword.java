package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ZireaelSword extends Item implements Listener {
    public ZireaelSword(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void equip(Player player) {
        super.equipItem(player, Material.DIAMOND_SWORD, ZireaelSword.class.getName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() != null) {
            String itemDisplayName = itemInMainHand.getItemMeta().getDisplayName();
            if (itemDisplayName.equals(ZireaelSword.class.getName())) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                        || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    blinkForward(event.getPlayer());
                }
            }
        }
    }

    // TODO: Create class for the effect
    private void blinkForward(Player player) {
        List<Block> sight = player.getLineOfSight(null, 10);
        Location targetLocation = sight.get(sight.size()-1).getLocation();  // TODO: Check if location is air
        player.teleport(targetLocation);
    }
}
