package cz.artin.hackers;

import java.util.logging.Logger;

public class MagicWand {
    private final static Logger LOGGER = Logger.getLogger(MagicWand.class.getName());

    public static boolean init() {
        LOGGER.info("MagicWand.init(): Started");
        LOGGER.info("MagicWand.init(): Finished");
        return true;
    }
}
