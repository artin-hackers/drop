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
        LOGGER.warning("Attempt to add mana as an item via Drop.ItemAdd interface");
    }

    public boolean add(Player player, Mana.Colour colour, int amount) {
        switch (colour) {
            case BLACK:
                return add(player, Material.BLACK_DYE, "Black Mana", amount, 5);
            case BLUE:
                return add(player, Material.BLUE_DYE, "Blue Mana", amount, 5);
            case GREEN:
                return add(player, Material.GREEN_DYE, "Green Mana", amount, 5);
            case RED:
                return add(player, Material.RED_DYE, "Red Mana", amount, 5);
            case WHITE:
                return add(player, Material.WHITE_DYE, "White Mana", amount, 5);
            default:
                LOGGER.warning("Unknown mana colour");
                return false;
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
        LOGGER.warning("Attempt to interact with mana");
    }

    enum Colour {
        BLACK,
        BLUE,
        GREEN,
        RED,
        WHITE
    }
}
