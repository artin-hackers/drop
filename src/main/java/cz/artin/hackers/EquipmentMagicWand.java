package cz.artin.hackers;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EquipmentMagicWand {
    public static void use(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_AIR)
                || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            EquipmentEffect.launchFireball(event.getPlayer());
        } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            EquipmentEffect.blinkForward(event.getPlayer());
        }
    }
}
