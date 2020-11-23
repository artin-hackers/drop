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

public abstract class Item implements Drop.ItemAdd {
    private static final Logger LOGGER = Logger.getLogger(Item.class.getName());

    Item() {
        LOGGER.finer("Item");
    }

    public abstract void interact(Player player, Action action);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (isItemInMainHand(event.getPlayer(), this.getClass().getName())) {
            interact(event.getPlayer(), event.getAction());
        }
    }

    public void add(Player player, Material material, String displayName) {
        add(player, material, displayName, 1, 64);
    }

    public boolean add(Player player, Material material, String displayName, int amount, int limit) {
        ItemStack itemStack = createItem(material, displayName, amount);
        if (itemStack == null) {
            return false;
        }
        if (player.getInventory().containsAtLeast(itemStack, limit)) {
            return false;
        } else {
            player.getInventory().addItem(itemStack);
            return true;
        }
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
            removeItems(player.getInventory(), displayName, amount);
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

    private void removeItems(Inventory inventory, String displayName, int amount) {
        int remainsToRemove = amount;
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack == null) {
                continue;
            }
            if (itemStack.getItemMeta() == null) {
                continue;
            }
            LOGGER.finest("Item.removeItems: material = " + itemStack.getType().toString() + ", displayName = " + itemStack.getItemMeta().getDisplayName());
            if (!itemStack.getItemMeta().getDisplayName().equals(displayName)) {
                continue;
            }

            if (itemStack.getAmount() == remainsToRemove) {
                inventory.clear(slot);
                break;
            } else if (itemStack.getAmount() > remainsToRemove) {
                itemStack.setAmount(itemStack.getAmount() - remainsToRemove);
                break;
            } else {
                inventory.clear(slot);
                remainsToRemove -= itemStack.getAmount();
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
