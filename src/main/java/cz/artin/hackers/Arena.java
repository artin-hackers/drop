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

        // Teleport players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int randomX = ThreadLocalRandom.current().nextInt(-5, 5);
            int randomZ = ThreadLocalRandom.current().nextInt(-5, 5);
            onlinePlayer.teleport(new Location(arenaCenter.getWorld(), arenaCenter.getX() + randomX, arenaCenter.getY(), arenaCenter.getZ() + randomZ));
        }

        // Clear the area
        for (int x = -50; x <= 50; x++) {
            for (int y = -2; y <= 30; y++) {
                for (int z = -50; z <= 50; z++) {
                    Location blockLocation = new Location(player.getWorld(), arenaCenter.getX() + x, arenaCenter.getY() + y, arenaCenter.getZ() + z);
                    blockLocation.getBlock().setType(Material.AIR);
                }
            }
        }

        // Build teleport platform
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                Location blockLocation = new Location(player.getWorld(), arenaCenter.getX() + x, arenaCenter.getY() + - 1, arenaCenter.getZ() + z);
                blockLocation.getBlock().setType(Material.EMERALD_BLOCK);
            }
        }

        // Build foundation
        for (int x = -50; x <= 50; x++) {
            for (int z = -50; z <= 50; z++) {
                Location blockLocation = new Location(player.getWorld(), arenaCenter.getX() + x, arenaCenter.getY() + - 3, arenaCenter.getZ() + z);
                blockLocation.getBlock().setType(Material.LAVA);
            }
        }

        // Build terrain
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                Location blockLocation = new Location(player.getWorld(), arenaCenter.getX() + x, arenaCenter.getY() + - 2, arenaCenter.getZ() + z);
                blockLocation.getBlock().setType(Material.GRASS_BLOCK);
            }
        }

        return true;
    }
}
