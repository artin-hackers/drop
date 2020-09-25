package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public abstract class Effect {
    private static final Logger LOGGER = Logger.getLogger(Effect.class.getName());

    public static void launchFireball(Player player) {
        LOGGER.info("launchFireball");
        player.launchProjectile(Fireball.class);
    }

    public static void blinkForward(Player player, Integer distance) {
        LOGGER.info("blinkForward");
        HashSet<Material> transparentMaterials = new HashSet<Material>();
        transparentMaterials.add(Material.AIR);
        transparentMaterials.add(Material.CAVE_AIR);
        transparentMaterials.add(Material.VOID_AIR);
        transparentMaterials.add(Material.WATER);
        List<Block> lineOfSight = player.getLineOfSight(transparentMaterials, distance);
        LOGGER.info("blinkForward: lineOfSight length = " + lineOfSight.size());
        Location targetLocation;
        if (lineOfSight.get(lineOfSight.size() - 1).getLocation().getBlock().getType().equals(Material.AIR)) {
            targetLocation = lineOfSight.get(lineOfSight.size() - 1).getLocation();
        } else {
            targetLocation = lineOfSight.get(lineOfSight.size() - 2).getLocation();
        }
        targetLocation.setDirection(player.getLocation().getDirection());
        player.teleport(targetLocation);
        player.setFallDistance(0);
    }
}
