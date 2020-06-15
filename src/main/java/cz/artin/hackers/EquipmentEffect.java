package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import java.util.List;

public class EquipmentEffect {
    public static void launchFireball(Player player) {
        player.launchProjectile(Fireball.class);
    }

    public static void blinkForward(Player player) {
        List<Block> sight = player.getLineOfSight(null, 10);
        Location targetLocation = sight.get(sight.size()-1).getLocation();
        player.teleport(targetLocation);
    }
}
