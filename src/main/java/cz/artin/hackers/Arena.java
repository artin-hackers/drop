package cz.artin.hackers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Arena {
    private static final Logger LOGGER = Logger.getLogger(Arena.class.getName());

    Arena() {
        LOGGER.finer("Arena");
    }

    public boolean buildArena(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            LOGGER.warning("Unexpected use of Arena.buildArena");
            return false;
        }

        Player player = (Player) commandSender;
        Location arenaCenter = new Location(player.getWorld(), -100, 70, 100);

        Location patchLocation = new Location(arenaCenter.getWorld(), arenaCenter.getX() - 50, arenaCenter.getY() - 5, arenaCenter.getZ() - 50);
        buildPatch(patchLocation, Material.AIR, 101, 40, 101);

        // Build foundation (lava + dirt)
        buildPatch(patchLocation, Material.LAVA, 101, 1, 101);
        patchLocation.add(0, 1, 0);
        buildPatch(patchLocation, Material.DIRT, 101, 2, 101);

        // Build terrain (grass)
        patchLocation.add(0, 2, 0);
        buildPatch(patchLocation, Material.GRASS_BLOCK, 101, 1, 101);

        // Build patches
        Material[] materials = {Material.DIRT, Material.GRASS_BLOCK, Material.STONE, Material.OAK_WOOD, Material.SAND, Material.GRAVEL, Material.WATER, Material.SNOW_BLOCK, Material.ICE, Material.ACACIA_WOOD, Material.BIRCH_WOOD, Material.DARK_OAK_WOOD, Material.JUNGLE_WOOD, Material.SPRUCE_WOOD};
        for (int i = getRandomInt(30, 50); i >= 0; i--) {
            patchLocation = new Location(arenaCenter.getWorld(), arenaCenter.getX() + getRandomInt(-50, 50), arenaCenter.getY() - 1, arenaCenter.getZ() + getRandomInt(-50, 50));
            buildPatch(patchLocation, materials[getRandomInt(materials.length)], getRandomInt(7), getRandomInt(5), getRandomInt(7));
        }

        // Build teleport platform
        patchLocation = new Location(arenaCenter.getWorld(), arenaCenter.getX() - 5, arenaCenter.getY() - 1, arenaCenter.getZ() - 5);
        buildPatch(patchLocation, Material.AIR, 11, 10, 11);
        buildPatch(patchLocation, Material.EMERALD_BLOCK, 11, 1, 11);

        // Teleport players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int randomX = ThreadLocalRandom.current().nextInt(-5, 5);
            int randomZ = ThreadLocalRandom.current().nextInt(-5, 5);
            onlinePlayer.teleport(new Location(arenaCenter.getWorld(), arenaCenter.getX() + randomX, arenaCenter.getY(), arenaCenter.getZ() + randomZ));
        }

        return true;
    }

    private void buildPatch(Location location, Material material, int sizeX, int sizeY, int sizeZ) {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    Location blockLocation = new Location(location.getWorld(), location.getX() + x, location.getY() + y, location.getZ() + z);
                    blockLocation.getBlock().setType(material);
                }
            }
        }
    }

    private int getRandomInt(int maximum) {
        return getRandomInt(1, maximum);
    }

    private int getRandomInt(int minimum, int maximum) {
        return ThreadLocalRandom.current().nextInt(minimum, maximum);
    }
}
