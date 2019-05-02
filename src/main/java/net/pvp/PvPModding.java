package net.pvp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PvPModding {

    public static Logger LOGGER = LogManager.getLogger("PvP");

    public static boolean isEnabled() {
        return true;
    }

}
