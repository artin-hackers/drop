package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public class Mana extends Item {
    private final Logger LOGGER = Logger.getLogger(Item.class.getName());

    public Mana() {
        LOGGER.finer("Mana()");
    }

    public void equip(Player player) {
    }

    public void interact(Player player, Action action) {
    }

    public boolean add(Player player, Colour colour, Integer amount) {
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
                return false;
        }
        return true;
    }

    public boolean remove(Player player, Colour colour, Integer amount) {
        switch (colour) {
            case BLACK:
                remove(player, Material.BLACK_DYE, "Black Mana", amount);
                break;
            case BLUE:
                remove(player, Material.BLUE_DYE, "Blue Mana", amount);
                break;
            case GREEN:
                remove(player, Material.GREEN_DYE, "Green Mana", amount);
                break;
            case RED:
                remove(player, Material.RED_DYE, "Red Mana", amount);
                break;
            case WHITE:
                remove(player, Material.WHITE_DYE, "White Mana", amount);
                break;
            default:
                LOGGER.warning("Unknown mana colour");
                return false;
        }
        return true;
    }

    public void addBlackMana(CommandSender sender, Integer amount) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack mana = new ItemStack(Material.BLACK_DYE, amount);
            player.getInventory().addItem(mana);
        }
    }

    public void addBlueMana(CommandSender sender, Integer amount) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack mana = new ItemStack(Material.BLUE_DYE, amount);
            player.getInventory().addItem(mana);
        }
    }

    public void addGreenMana(CommandSender sender, Integer amount) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack mana = new ItemStack(Material.GREEN_DYE, amount);
            player.getInventory().addItem(mana);
        }
    }

    public void addRedMana(CommandSender sender, Integer amount) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack mana = new ItemStack(Material.RED_DYE, amount);
            player.getInventory().addItem(mana);
        }
    }

    public void addWhiteMana(CommandSender sender, Integer amount) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack mana = new ItemStack(Material.WHITE_DYE, amount);
            player.getInventory().addItem(mana);
        }
    }

    public boolean removeBlackMana(CommandSender sender, Integer amount) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getInventory().containsAtLeast(new ItemStack(Material.BLACK_DYE), amount)) {
                removeItems(player.getInventory(), Material.BLACK_DYE, amount);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean removeBlueMana(CommandSender sender, Integer amount) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getInventory().containsAtLeast(new ItemStack(Material.BLUE_DYE), amount)) {
                removeItems(player.getInventory(), Material.BLUE_DYE, amount);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean removeGreenMana(CommandSender sender, Integer amount) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getInventory().containsAtLeast(new ItemStack(Material.GREEN_DYE), amount)) {
                removeItems(player.getInventory(), Material.GREEN_DYE, amount);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean removeRedMana(CommandSender sender, Integer amount) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getInventory().containsAtLeast(new ItemStack(Material.RED_DYE), amount)) {
                removeItems(player.getInventory(), Material.RED_DYE, amount);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean removeWhiteMana(CommandSender sender, Integer amount) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getInventory().containsAtLeast(new ItemStack(Material.WHITE_DYE), amount)) {
                removeItems(player.getInventory(), Material.WHITE_DYE, amount);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) {
            return;
        }
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) {
                continue;
            }
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) {
                        break;
                    }
                }
            }
        }
    }

    enum Colour {
        BLACK,
        BLUE,
        GREEN,
        RED,
        WHITE
    }
}
