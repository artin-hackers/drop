package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public abstract class Effect {
    private static final Logger LOGGER = Logger.getLogger(Effect.class.getName());

    public static void addMana(Player player, Mana.Colour colour, Integer amount) {
        Mana mana = new Mana();
        mana.add(player, colour, amount);
    }

    public static void removeMana(Player player, Mana.Colour colour, Integer amount) {
        Mana mana = new Mana();
        mana.remove(player, colour, amount);
    }

    public static void blinkForward(Player player, Integer distance) {
        LOGGER.finer("blinkForward()");
        HashSet<Material> transparentMaterials = new HashSet<>(Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.WATER));
        List<Block> lineOfSight = player.getLineOfSight(transparentMaterials, distance);
        LOGGER.fine("blinkForward(): lineOfSight length = " + lineOfSight.size());
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
        LOGGER.finer("dealDamage()");
        player.damage(amount);
    }

    public static void launchFireball(Player player) {
        LOGGER.finer("launchFireball()");
        player.launchProjectile(Fireball.class);
    }
}
