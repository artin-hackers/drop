package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Mana {
    enum Colours {
        BLACK,
        BLUE,
        GREEN,
        RED,
        WHITE
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
}
