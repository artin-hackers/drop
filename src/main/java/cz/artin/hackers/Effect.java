package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Effect {
    public static void launchFireball(Player player) {
        player.launchProjectile(Fireball.class);
    }

    public static void blinkForward(Player player, Integer distance) {
        List<Block> sight = player.getLineOfSight(null, distance);
        Location targetLocation;
        if (sight.get(sight.size() - 1).getLocation().getBlock().getType().equals(Material.AIR)) {
            targetLocation = sight.get(sight.size() - 1).getLocation();
        } else {
            targetLocation = sight.get(sight.size() - 2).getLocation();
        }
        targetLocation.setDirection(player.getLocation().getDirection());
        player.teleport(targetLocation);
        player.setFallDistance(0);
    }
}
