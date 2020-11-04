package cz.artin.hackers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

public abstract class Item implements Drop.ItemEquip {
    private final Logger LOGGER = Logger.getLogger(Item.class.getName());

    public Item() {
        LOGGER.finer("Item()");
    }

    public abstract void interact(Player player, Action action);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (isItemInMainHand(event.getPlayer(), this.getClass().getName())) {
            interact(event.getPlayer(), event.getAction());
        }
    }

    public void add(Player player, Material material, String displayName) {
        add(player, material, displayName, 1);
    }

    public void add(Player player, Material material, String displayName, Integer amount) {
        ItemStack itemStack = createItem(material, displayName, amount);
        if (itemStack == null) {
            return;
        }
        player.getInventory().addItem(itemStack);
    }

    public boolean remove(Player player, Material material, String displayName) {
        return remove(player, material, displayName, 1);
    }

    public boolean remove(Player player, Material material, String displayName, Integer amount) {
        if (amount <= 0) {
            LOGGER.warning("Invalid amount");
            return false;
        }
        ItemStack itemStack = createItem(material, displayName);
        if (itemStack == null) {
            return false;
        }

        if (player.getInventory().containsAtLeast(itemStack, amount)) {
            removeItems(player, material, displayName, amount);
            return true;
        } else {
            return false;
        }
    }

    private ItemStack createItem(Material material, String displayName) {
        return createItem(material, displayName, 1);
    }

    private ItemStack createItem(Material material, String displayName, Integer amount) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            LOGGER.warning("Cannot get item meta data");
            return null;
        }
        meta.setDisplayName(displayName);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void removeItems(Player player, Material material, String displayName, Integer amount) {
        Integer remainsToRemove = amount;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack slotItemStack = inventory.getItem(slot);
            if (slotItemStack == null) {
                continue;
            }
            if (slotItemStack.getItemMeta() == null) {
                continue;
            }
            if (slotItemStack.getType() != material) {
                continue;
            }
            if (!slotItemStack.getItemMeta().getDisplayName().equals(displayName)) {
                continue;
            }

            int newAmount = slotItemStack.getAmount() - remainsToRemove;
            if (newAmount > 0) {
                slotItemStack.setAmount(newAmount);
                break;
            } else {
                inventory.clear(slot);
                remainsToRemove = -newAmount;
                if (remainsToRemove == 0) {
                    break;
                }
            }
        }
    }

    private boolean isItemInMainHand(Player player, String displayName) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() != null) {
            return itemInMainHand.getItemMeta().getDisplayName().equals(displayName);
        } else {
            return false;
        }
    }
}
