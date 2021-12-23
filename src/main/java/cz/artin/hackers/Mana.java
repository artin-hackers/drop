package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.logging.Logger;

public class Mana extends Item {
    private static final Logger LOGGER = Logger.getLogger(Mana.class.getName());

    Mana() {
        LOGGER.finer("Mana");
    }

    public void add(Player player) {
        LOGGER.warning("Attempt to add mana as an item via Drop.ItemAdd interface");
    }

    public void add(Player player, Mana.Colour colour, int amount) {
        switch (colour) {
            case BLACK:
                add(player, Material.OBSIDIAN, "Black Mana", amount, 20);
                break;
            case BLUE:
                add(player, Material.LAPIS_BLOCK, "Blue Mana", amount, 20);
                break;
            case GREEN:
                add(player, Material.GREEN_CONCRETE, "Green Mana", amount, 20);
                break;
            case RED:
                add(player, Material.REDSTONE_BLOCK, "Red Mana", amount, 20);
                break;
            case WHITE:
                add(player, Material.SNOW_BLOCK, "White Mana", amount, 20);
                break;
            default:
                LOGGER.warning("Unknown mana colour");
        }
    }

    public boolean remove(Player player, Mana.Colour colour, int amount) {
        switch (colour) {
            case BLACK:
                return remove(player, Material.OBSIDIAN, "Black Mana", amount);
            case BLUE:
                return remove(player, Material.LAPIS_BLOCK, "Blue Mana", amount);
            case GREEN:
                return remove(player, Material.GREEN_CONCRETE, "Green Mana", amount);
            case RED:
                return remove(player, Material.REDSTONE_BLOCK, "Red Mana", amount);
            case WHITE:
                return remove(player, Material.SNOW_BLOCK, "White Mana", amount);
            default:
                LOGGER.warning("Unknown mana colour");
                return false;
        }
    }

    public void interact(PlayerInteractEvent event) {
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
