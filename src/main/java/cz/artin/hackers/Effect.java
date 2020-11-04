package cz.artin.hackers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public abstract class Effect {
    private static final Logger LOGGER = Logger.getLogger(Effect.class.getName());

    public static void addMana(Player player, Mana.Colours colour, Integer amount) {
        LOGGER.finer("addMana()");
        Material manaMaterial;
        String displayName;
        switch (colour) {
            case BLACK:
                manaMaterial = Material.BLACK_DYE;
                displayName = "Black Mana";
                break;
            case BLUE:
                manaMaterial = Material.BLUE_DYE;
                displayName = "Blue Mana";
                break;
            case GREEN:
                manaMaterial = Material.GREEN_DYE;
                displayName = "Green Mana";
                break;
            case RED:
                manaMaterial = Material.RED_DYE;
                displayName = "Red Mana";
                break;
            case WHITE:
                manaMaterial = Material.WHITE_DYE;
                displayName = "White Mana";
                break;
            default:
                LOGGER.warning("addMana(): Unknown mana colour");
                return;
        }
        ItemStack mana = new ItemStack(manaMaterial, amount);
        ItemMeta meta = mana.getItemMeta();
        if (meta == null) {
            LOGGER.warning("Cannot get item meta data");
            return;
        }
        meta.setDisplayName(displayName);
        mana.setItemMeta(meta);
        player.getInventory().addItem(mana);
    }

    public static boolean removeMana(Player player, Mana.Colours colour, Integer amount) {
        LOGGER.finer("removeMana()");
        Material manaMaterial;
        String displayName;
        switch (colour) {
            case BLACK:
                manaMaterial = Material.BLACK_DYE;
                displayName = "Black Mana";
                break;
            case BLUE:
                manaMaterial = Material.BLUE_DYE;
                displayName = "Blue Mana";
                break;
            case GREEN:
                manaMaterial = Material.GREEN_DYE;
                displayName = "Green Mana";
                break;
            case RED:
                manaMaterial = Material.RED_DYE;
                displayName = "Red Mana";
                break;
            case WHITE:
                manaMaterial = Material.WHITE_DYE;
                displayName = "White Mana";
                break;
            default:
                LOGGER.warning("addMana(): Unknown mana colour");
                return false;
        }
        ItemStack mana = new ItemStack(manaMaterial);
        ItemMeta meta = mana.getItemMeta();
        if (meta == null) {
            LOGGER.warning("Cannot get item meta data");
            return false;
        }
        meta.setDisplayName(displayName);
        mana.setItemMeta(meta);

        if (player.getInventory().containsAtLeast(mana, amount)) {
            removeItems(player.getInventory(), manaMaterial, amount);
            return true;
        } else {
            return false;
        }
    }

    private static void removeItems(Inventory inventory, Material type, Integer amount) {
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

    public static void blinkForward(Player player, Integer distance) {
        LOGGER.finer("blinkForward()");
        HashSet<Material> transparentMaterials = new HashSet<>(Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.WATER));
        List<Block> lineOfSight = player.getLineOfSight(transparentMaterials, distance);
        LOGGER.fine("blinkForward(): lineOfSight length = " + lineOfSight.size());
        Location targetLocation;
        if (lineOfSight.get(lineOfSight.size() - 1).getLocation().getBlock().getType().equals(Material.AIR)) {
            targetLocation = lineOfSight.get(lineOfSight.size() - 1).getLocation();
        } else {
            targetLocation = lineOfSight.get(lineOfSight.size() - 2).getLocation();
        }
        targetLocation.setDirection(player.getLocation().getDirection());
        player.teleport(targetLocation);
        player.setFallDistance(0);
    }

    public static void dealDamage(Player player, Integer amount) {
        LOGGER.finer("dealDamage()");
        player.damage(amount);
    }

    public static void launchFireball(Player player) {
        LOGGER.finer("launchFireball()");
        player.launchProjectile(Fireball.class);
    }
}
