package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public abstract class Effect {
    private static final Logger LOGGER = Logger.getLogger(Effect.class.getName());

    Effect() {
        LOGGER.finer("Effect");
    }

    public static void addMana(Player player, Mana.Colour colour, Integer amount) {
        (new Mana()).add(player, colour, amount);
    }

    public static boolean removeMana(Player player, Mana.Colour colour, Integer amount) {
        return (new Mana()).remove(player, colour, amount);
    }

    public static void blinkForward(Player player, Integer distance) {
        HashSet<Material> transparentMaterials = new HashSet<>(Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.WATER));
        List<Block> lineOfSight = player.getLineOfSight(transparentMaterials, distance);
        LOGGER.fine("Effect.blinkForward: lineOfSight length = " + lineOfSight.size());
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

    public static void dealDamage(Player player, Integer amount) {
        player.damage(amount);
    }

    public static void launchFireball(Player player) {
        player.launchProjectile(Fireball.class);
    }

    public static void creategauge(Player player) {
        Location playerLocation = player.getLocation();
        Location playergroundLocation = playerLocation.add(0, -1, 0);
        Material material = playergroundLocation.getBlock().getType();
        Set<Material> all_materials = new HashSet<>();
        all_materials.add(Material.GOLD_ORE);
        for (Material mat : Material.values()) {
            all_materials.add(mat);
        }
        List<Block> sight = player.getLineOfSight(all_materials, 10);
//            for (int i = 0; i < sight.size(); i++) {
//                if (i > sight.size() / 4) {
//                    sight.get(i).setType(material);
//                }
//            }
        Location wallCentre = sight.get(sight.size() - 1).getLocation();
        wallCentre.getBlock().setType(Material.GOLD_BLOCK);
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    final Location wallBlock = new Location(
                            player.getWorld(),
                            wallCentre.getX() + x,
                            wallCentre.getY() + y,
                            wallCentre.getZ() + z);
                    wallBlock.getBlock().setType(material);
                }
            }
        }
    }


    public static void createHole(Player player) {
        List<Block> sight = player.getLineOfSight(null, 20);
        Location holeCentre = sight.get(sight.size() - 1).getLocation();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    final Location wallBlock = new Location(
                            player.getWorld(),
                            holeCentre.getX() + x,
                            holeCentre.getY() + y,
                            holeCentre.getZ() + z);
                    wallBlock.getBlock().setType(Material.AIR);
                }
            }
        }
    }
}
