package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.logging.Logger;

public class Mana extends Item {
    private static final Logger LOGGER = Logger.getLogger(Mana.class.getName());

    Mana() {
        LOGGER.finer("Mana");
    }

    public void add(Player player) {
    }

    public void add(Player player, Mana.Colour colour, int amount) {
        switch (colour) {
            case BLACK:
                add(player, Material.BLACK_DYE, "Black Mana", amount);
                break;
            case BLUE:
                add(player, Material.BLUE_DYE, "Blue Mana", amount);
                break;
            case GREEN:
                add(player, Material.GREEN_DYE, "Green Mana", amount);
                break;
            case RED:
                add(player, Material.RED_DYE, "Red Mana", amount);
                break;
            case WHITE:
                add(player, Material.WHITE_DYE, "White Mana", amount);
                break;
            default:
                LOGGER.warning("Unknown mana colour");
        }
    }

    public boolean remove(Player player, Mana.Colour colour, int amount) {
        switch (colour) {
            case BLACK:
                return remove(player, Material.BLACK_DYE, "Black Mana", amount);
            case BLUE:
                return remove(player, Material.BLUE_DYE, "Blue Mana", amount);
            case GREEN:
                return remove(player, Material.GREEN_DYE, "Green Mana", amount);
            case RED:
                return remove(player, Material.RED_DYE, "Red Mana", amount);
            case WHITE:
                return remove(player, Material.WHITE_DYE, "White Mana", amount);
            default:
                LOGGER.warning("Unknown mana colour");
                return false;
        }
    }

    public void interact(Player player, Action action) {
    }

    enum Colour {
        BLACK,
        BLUE,
        GREEN,
        RED,
        WHITE
    }
}
