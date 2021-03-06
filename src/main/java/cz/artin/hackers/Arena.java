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
    private static final int DEFAULT_LOBBY_SIZE = 20;
    private static final int DEFAULT_LOBBY_HEIGHT = 5;
    private static final int DEFAULT_ARENA_LOBBY_DISTANCE = 31;
    private static final int DEFAULT_TELEPORT_PLATFORM_SIZE = 11;
    private static final Material DEFAULT_LOBBY_MATERIAL = Material.GLASS;
    private static Location arenaCenter = null;
    private static Location lobbyCenter = null;

    Arena() {
        LOGGER.finer("Arena");
    }

    public boolean isInitialised() {
        if (arenaCenter != null && lobbyCenter != null) {
            return true;
        } else {
            return false;
        }
    }

    public Location getArenaCenter() {
        return arenaCenter;
    }

    public void setArenaCenter(Location location) {
        arenaCenter = location;
        lobbyCenter = arenaCenter.clone();
        lobbyCenter.add(0, DEFAULT_ARENA_LOBBY_DISTANCE, 0);
    }

    public Location getLobbyCenter() {
        return lobbyCenter;
    }

    public Location getLobbyRandomLocation() {
        return new Location(lobbyCenter.getWorld(), getRandomPositionWithin(lobbyCenter.getX(), DEFAULT_LOBBY_SIZE), lobbyCenter.getY() + 1, getRandomPositionWithin(lobbyCenter.getZ(), DEFAULT_LOBBY_SIZE));
    }

    public Location getArenaRandomLocation() {
        return new Location(arenaCenter.getWorld(), getRandomPositionWithin(arenaCenter.getX(), DEFAULT_TELEPORT_PLATFORM_SIZE), arenaCenter.getY() + 2, getRandomPositionWithin(arenaCenter.getZ(), DEFAULT_TELEPORT_PLATFORM_SIZE));
    }

    public void createLobby() {
        if (getLobbyCenter() == null) {
            return;
        }

        Material lobbyMaterial = DEFAULT_LOBBY_MATERIAL;
        Location lobbyCorner = new Location(getLobbyCenter().getWorld(), getMinimumPositionWithin(getLobbyCenter().getX(), DEFAULT_LOBBY_SIZE), getLobbyCenter().getY(), getMinimumPositionWithin(getLobbyCenter().getZ(), DEFAULT_LOBBY_SIZE));

        // Clearance
        buildPatch(lobbyCorner, Material.AIR, DEFAULT_LOBBY_SIZE, DEFAULT_LOBBY_HEIGHT + 1, DEFAULT_LOBBY_SIZE);

        // Build floor
        buildPatch(lobbyCorner, lobbyMaterial, DEFAULT_LOBBY_SIZE, 1, DEFAULT_LOBBY_SIZE);

        // Build columns
        for (int i = 1; i <= DEFAULT_LOBBY_HEIGHT; i++) {
            (new Location(lobbyCorner.getWorld(), lobbyCorner.getX(), lobbyCorner.getY() + i, lobbyCorner.getZ())).getBlock().setType(lobbyMaterial);
            (new Location(lobbyCorner.getWorld(), lobbyCorner.getX() + DEFAULT_LOBBY_SIZE - 1, lobbyCorner.getY() + i, lobbyCorner.getZ())).getBlock().setType(lobbyMaterial);
            (new Location(lobbyCorner.getWorld(), lobbyCorner.getX(), lobbyCorner.getY() + i, lobbyCorner.getZ() + DEFAULT_LOBBY_SIZE - 1)).getBlock().setType(lobbyMaterial);
            (new Location(lobbyCorner.getWorld(), lobbyCorner.getX() + DEFAULT_LOBBY_SIZE - 1, lobbyCorner.getY() + i, lobbyCorner.getZ() + DEFAULT_LOBBY_SIZE - 1)).getBlock().setType(lobbyMaterial);
        }

        // Build top frame
        for (int i = 0; i < DEFAULT_LOBBY_SIZE; i++) {
            (new Location(lobbyCorner.getWorld(), lobbyCorner.getX() + i, lobbyCorner.getY() + DEFAULT_LOBBY_HEIGHT, lobbyCorner.getZ())).getBlock().setType(lobbyMaterial);
            (new Location(lobbyCorner.getWorld(), lobbyCorner.getX() + i, lobbyCorner.getY() + DEFAULT_LOBBY_HEIGHT, lobbyCorner.getZ() + DEFAULT_LOBBY_SIZE - 1)).getBlock().setType(lobbyMaterial);
            (new Location(lobbyCorner.getWorld(), lobbyCorner.getX(), lobbyCorner.getY() + DEFAULT_LOBBY_HEIGHT, lobbyCorner.getZ() + i)).getBlock().setType(lobbyMaterial);
            (new Location(lobbyCorner.getWorld(), lobbyCorner.getX() + +DEFAULT_LOBBY_SIZE - 1, lobbyCorner.getY() + DEFAULT_LOBBY_HEIGHT, lobbyCorner.getZ() + i)).getBlock().setType(lobbyMaterial);
        }
    }

    public boolean buildArena() {

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
        buildPatch(patchLocation, Material.AIR, DEFAULT_TELEPORT_PLATFORM_SIZE, 10, DEFAULT_TELEPORT_PLATFORM_SIZE);
        buildPatch(patchLocation, Material.EMERALD_BLOCK, DEFAULT_TELEPORT_PLATFORM_SIZE, 1, DEFAULT_TELEPORT_PLATFORM_SIZE);
        
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

    private double getRandomPositionWithin(double center, int size) {
        int radius = (size >> 1) - 1;
        return center + getRandomInt(-radius, radius);
    }

    private double getMinimumPositionWithin(double center, int size) {
        int radius = size >> 1;
        return center - radius;
    }
}
