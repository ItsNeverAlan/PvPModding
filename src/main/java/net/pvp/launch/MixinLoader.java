package net.pvp.launch;

import net.minecraftforge.fml.relauncher.*;
import java.util.*;
import org.spongepowered.asm.launch.*;
import org.spongepowered.asm.mixin.*;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class MixinLoader implements IFMLLoadingPlugin
{
    public String getSetupClass() {
        return null;
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public void injectData(final Map<String, Object> data) {
    }

    public String getAccessTransformerClass() {
        return null;
    }

    public MixinLoader() {
        System.out.println("Injecting with IFMLLoadingPlugin.");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.pvp.json");
    }
}
