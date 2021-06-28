package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

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
        // Fireball fireball = Fireball();
        // player.launchProjectile(fireball);
        player.launchProjectile(Fireball.class);
    }
    public static void createFireLine(Player player) {
        List<Block> sight = player.getLineOfSight(null, 10);
        int i = 0;
        for (Block block : sight) {
            if (block.getType().equals(Material.AIR)) {
                Location location = block.getLocation();
                for (i = 0; i < 10; i++) {
                    Block blockBelow = location.getBlock();
                    if (blockBelow.getType().equals(Material.AIR)) {
                        location.add(0, -1, 0);
                    } else {
                        location.getBlock().setType(Material.LAVA);
                        break;
                    }
                }
            } else {
                block.setType(Material.LAVA);
            }
        }
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

//        maxLineOfSight = 5 + 2 * level;
//        if (maxLineOfSight > 25) {
            maxLineOfSight = 10;
//        }
        List<Block> sight = player.getLineOfSight(null, maxLineOfSight);

//        wallSize = 1 + level;
//        if (wallSize > 5) {
            wallSize = 3;
//
        Location playerLocation  = player.getLocation();
        playerLocation.add(0,-1,0);
        Material material = playerLocation.getBlock().getType();
        // wallBlock.getBlock().setType(material);

        // 1. Get player location: playerLocation = player.getLocation()
        // 2. Get one block below: playerLocation.add(0, -1, 0)
        // 3. Get material from the block: material = playerLocation.getBlock().getType()
        // 4. Use material instead of gold: wallBlock.getBlock().setType(Material.GOLD_BLOCK) -> wallBlock.getBlock().setType(material)

        Location wallCentre = sight.get(sight.size() - 1).getLocation();
        Location wallCorner = wallCentre.clone();
        wallCorner.add(-(int) (wallSize / 2), -(int) (wallSize / 2), -(int) (wallSize / 2));

        for (int x = 0; x < wallSize; x++) {
            for (int y = 0; y < wallSize; y++) {
                for (int z = 0; z < wallSize; z++) {
                    final Location wallBlock = new Location(
                            player.getWorld(),
                            wallCorner.getX() + x,
                            wallCorner.getY() + y,
                            wallCorner.getZ() + z);
                    wallBlock.getBlock().setType(material);
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

    public static void spawnZombies(Location location) {
        Location zombieLocation = location.clone();
        zombieLocation.add(0,1,0);
        zombieLocation.getWorld().spawn(zombieLocation, Zombie.class);    
    }

    public static void createHole(Player player) {
        int level = player.getLevel();
        int maxLineOfSight;
        int holeSize;

        // maxLineOfSight = 5 + 2 * level;
        // if (maxLineOfSight > 25) {
            maxLineOfSight = 10;
        // }
        List<Block> sight = player.getLineOfSight(null, maxLineOfSight);

        // holeSize = 1 + level;
        // if (holeSize > 5 ) {
            holeSize = 3;
        // }

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

    public static void launchArrow(Player player) {
        player.launchProjectile(Arrow.class);
    }
}
