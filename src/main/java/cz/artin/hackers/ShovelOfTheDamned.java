package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ShovelOfTheDamned extends Item implements Listener {
    private static final Logger LOGGER = Logger.getLogger(ZireaelSword.class.getName());

    ShovelOfTheDamned(JavaPlugin plugin) {
        LOGGER.finer("ShovelOfTheDamned");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void add(Player player) {
        add(player, Material.DIAMOND_SHOVEL, ShovelOfTheDamned.class.getName());
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Player shooter = (Player) event.getEntity().getShooter();
            if (shooter != null) {
                ItemStack itemInMainHand = shooter.getInventory().getItemInMainHand();
                if (itemInMainHand.getItemMeta() != null && itemInMainHand.getItemMeta().getDisplayName().equals("cz.artin.hackers.ShovelOfTheDamned")) {
                    LOGGER.info("ShovelOfTheDamned");
                    if (event.getHitBlock() != null) {
                        Effect.spawnZombies(event.getHitBlock().getLocation());
                    } else if (event.getHitEntity() != null) {
                        Effect.spawnZombies(event.getHitEntity().getLocation());
                    }
                }
            }
        }
    }

    public void interact(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (Effect.removeMana(player, Mana.Colour.BLACK, 1)) {
               // Effect.spawnZombies(player);
                Effect.launchSnowball(player);
            } else {
                player.sendMessage("Not enough mana");
            }
        }
    }
}
