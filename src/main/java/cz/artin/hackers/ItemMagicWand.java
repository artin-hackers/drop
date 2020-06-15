package cz.artin.hackers;

import java.util.logging.Logger;

public class ItemMagicWand {
    private final static Logger LOGGER = Logger.getLogger(ItemMagicWand.class.getName());

    public static boolean init() {
        LOGGER.info("MagicWand.init(): Started");
        LOGGER.info("MagicWand.init(): Finished");
        return true;
    }

    public static boolean equip() {
        LOGGER.info("MagicWand.equip(): Started");
        LOGGER.info("MagicWand.equip(): Finished");
        return true;
    }
}
