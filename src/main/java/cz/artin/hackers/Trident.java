package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class Trident extends Item {
    public void add(Player player) {
        add(player, Material.TRIDENT,Trident.class.getName());
    }

    public void interact(Player player, Action action) {
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (Effect.removeMana(player, Mana.Colour.WHITE, 1)) {
                Effect.launchFireball(player);
            } else {
                player.sendMessage("Not enough mana");
            }
        }
    }
}
