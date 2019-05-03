package net.pvp;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PvPModding {

    public static Logger LOGGER = LogManager.getLogger("PvP");

    public static boolean isEnabled() {
        return /*true || */isGameruleEnabled();
    }

    /* TEST */
    public static boolean isGameruleEnabled() {
        return Minecraft.getMinecraft().getIntegratedServer() != null
                && Minecraft.getMinecraft().getIntegratedServer().getEntityWorld()
                .getGameRules().hasRule("oldCombatMechanics")
                && Minecraft.getMinecraft().getIntegratedServer().getEntityWorld()
                .getGameRules().getBoolean("oldCombatMechanics");
    }

}
