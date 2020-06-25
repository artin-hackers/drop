package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ZireaelSword extends Item implements Listener {
    public ZireaelSword(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void equip(Player player) {
        super.equip(player, Material.DIAMOND_SWORD, ZireaelSword.class.getName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isItemInMainHand(event, ZireaelSword.class.getName())) {
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            blinkForward(event.getPlayer());
        }
    }

    // TODO: Create class for the effect
    private void blinkForward(Player player) {
        List<Block> sight = player.getLineOfSight(null, 10);
        Location targetLocation = sight.get(sight.size()-1).getLocation();
        if (targetLocation.getBlock().getType().equals(Material.AIR)) {
        } else if (sight.size() >= 2) {
            targetLocation = sight.get(sight.size()-2).getLocation();
        } else {
            return;
        }
        player.teleport(targetLocation);
    }
}
