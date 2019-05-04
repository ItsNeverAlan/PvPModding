package net.pvp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "pvpmodding", name = "PvPModding", version = "1.0")
public class PvPModding {

    @Mod.Instance
    public static PvPModding INSTANCE;

    public static Logger LOGGER = LogManager.getLogger("PvP");

    public static boolean isEnabled() {
        return /*true || */INSTANCE.isGameruleEnabled();
    }

    /* TEST */
    public boolean isGameruleEnabled() {
        if (Minecraft.getMinecraft().isSingleplayer())
            return FMLCommonHandler.instance().getMinecraftServerInstance() != null
                    && FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld()
                    .getGameRules().hasRule("oldCombatMechanics")
                    && FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld()
                    .getGameRules().getBoolean("oldCombatMechanics");
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            if (player.getServer() != null) {
                WorldServer worldServer = player.getServer().getWorld(0);
                if (worldServer.getGameRules().hasRule("oldCombatMechanics")) {
                    return worldServer.getGameRules().getBoolean("oldCombatMechanics");
                }
            }
        }
        LOGGER.info("thinking");
        return false;
    }

}
