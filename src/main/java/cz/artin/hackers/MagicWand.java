package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MagicWand extends Item implements Listener {
    public MagicWand(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void equip(Player player) {
        equip(player, Material.STICK, MagicWand.class.getName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isItemInMainHand(event, MagicWand.class.getName())) {
            return;
        }
        if (event.getAction().equals(Action.LEFT_CLICK_AIR)
                || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            launchFireball(event.getPlayer());
        }
    }

    // TODO: Create class for the effect
    private void launchFireball(Player player) {
        player.launchProjectile(Fireball.class);
    }
}
