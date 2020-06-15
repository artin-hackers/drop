package cz.artin.hackers;

import org.bukkit.entity.Fireball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EquipmentMagicWand {
    public static void use(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_AIR)
                || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            event.getPlayer().launchProjectile(Fireball.class);
        }
    }
}
