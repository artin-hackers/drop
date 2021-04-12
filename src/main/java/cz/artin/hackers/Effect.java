package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Snowball;
import java.util.*;
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

    public static void launchSnowball(Player player) {
        player.launchProjectile(Snowball.class);
    }

    public static void addHealth(Player player, Integer amount) {
        double health = player.getHealth();
        if (health < 20.0) {
            if (Effect.removeMana(player, Mana.Colour.WHITE, 1)) {
                player.setHealth(health + ((double) amount));
                player.sendMessage("You are healed");
            } else {
                player.sendMessage("Not enough mana");
            }
        } else {
            player.sendMessage("You are on max health already");
        }
    }

    public static void creategauge(Player player) {
        int level = player.getLevel();
        int maxLineOfSight;
        int wallSize;

        maxLineOfSight = 5 + 2 * level;
        if (maxLineOfSight > 25) {
            maxLineOfSight = 25;
        }
        List<Block> sight = player.getLineOfSight(null, maxLineOfSight);

        wallSize = 1 + level;
        if (wallSize > 5 ) {
            wallSize = 5;
        }

        Location wallCentre = sight.get(sight.size() - 1).getLocation();
        Location wallCorner = wallCentre.clone();
        wallCorner.add(-(int) (wallSize / 2), -(int) (wallSize / 2), -(int) (wallSize / 2));

        for (int x = 0; x <= wallSize; x++) {
            for (int y = 0; y <= wallSize; y++) {
                for (int z = 0; z <= wallSize; z++) {
                    final Location wallBlock = new Location(
                            player.getWorld(),
                            wallCorner.getX() + x,
                            wallCorner.getY() + y,
                            wallCorner.getZ() + z);
                    wallBlock.getBlock().setType(Material.GOLD_BLOCK);
                }
            }
        }
    }

    public static void spawnZombies(Player player) {
        int distance = 11;
        List<Block> sight = player.getLineOfSight(null, distance);
        Location zombieLocation = sight.get(sight.size() - 1).getLocation();
        player.getWorld().spawn(zombieLocation, Zombie.class);
    }

    public static void createHole(Player player) {
        int level = player.getLevel();
        int maxLineOfSight;
        int holeSize;

        maxLineOfSight = 5 + 2 * level;
        if (maxLineOfSight > 25) {
            maxLineOfSight = 25;
        }
        List<Block> sight = player.getLineOfSight(null, maxLineOfSight);

        holeSize = 1 + level;
        if (holeSize > 5 ) {
            holeSize = 5;
        }

        Location holeCentre = sight.get(sight.size() - 1).getLocation();
        Location holeCorner = holeCentre.clone();
        holeCorner.add(-(int) (holeSize / 2), -(int) (holeSize / 2), -(int) (holeSize / 2));

        for (int x = 0; x < holeSize; x++) {
            for (int y = 0; y < holeSize; y++) {
                for (int z = 0; z < holeSize; z++) {
                    final Location holeBlock = new Location(
                            player.getWorld(),
                            holeCorner.getX() + x,
                            holeCorner.getY() + y,
                            holeCorner.getZ() + z);
                    holeBlock.getBlock().setType(Material.AIR);
                }
            }
        }
    }
}
