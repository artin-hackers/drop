package cz.artin.hackers;

import org.bukkit.event.player.PlayerInteractEvent;

import java.util.logging.Logger;

public class ItemMagicWand {
    private final static Logger LOGGER = Logger.getLogger(ItemMagicWand.class.getName());

    public static void effect(PlayerInteractEvent event) {
        LOGGER.info("ItemMagicWand.effect: Started");
        LOGGER.info("ItemMagicWand.effect: Finished");
    }
}
