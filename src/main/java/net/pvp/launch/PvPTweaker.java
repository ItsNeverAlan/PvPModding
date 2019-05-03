package net.pvp.launch;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.pvp.PvPModding;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PvPTweaker implements ITweaker {

    public static PvPTweaker INSTANCE;

    private ArrayList<String> args = new ArrayList<>();

    private boolean isRunningForge = Launch.classLoader.getTransformers().stream()
            .anyMatch(p -> p.getClass().getName().contains("fml"));

    private boolean isRunningOptifine = Launch.classLoader.getTransformers().stream()
            .anyMatch(p -> p.getClass().getName().contains("optifine"));

    private boolean FORGE = false;
    private boolean OPTIFINE = false;

    public PvPTweaker() {
        INSTANCE = this;
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, final File assetsDir,
                              String profile) {
        this.args.addAll(args);

        addArg("gameDir", gameDir);
        addArg("assetsDir", assetsDir);
        addArg("version", profile);
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {

        PvPModding.LOGGER.info("Initialising Bootstraps...");
        MixinBootstrap.init();

        PvPModding.LOGGER.info("Applying transformers...");

        // Excludes packages from classloader
        MixinEnvironment environment = MixinEnvironment.getDefaultEnvironment();
        Mixins.addConfiguration("mixins.pvp.json");

        if (this.isRunningForge) {
            this.FORGE = true;
            environment.setObfuscationContext("searge");
        }

        if (this.isRunningOptifine) {
            this.OPTIFINE = true;

            environment.setObfuscationContext("notch");
        }

        if (environment.getObfuscationContext() == null) {
            environment.setObfuscationContext("notch");
        }

        PvPModding.LOGGER.info("Forge {}!", FORGE ? "found" : "not found");

        environment.setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String[] getLaunchArguments() {
        if (FORGE || OPTIFINE) {
            return new String[0];
        } else {
            return args.toArray(new String[]{});
        }
    }

    public boolean isUsingForge() {
        return FORGE;
    }

    public boolean isUsingOptifine() {
        return OPTIFINE;
    }

    private void addArg(String label, String value) {
        if (!this.args.contains(("--" + label)) && value != null) {
            this.args.add(("--" + label));
            this.args.add(value);
        }
    }

    private void addArg(String args, File file) {
        if (file == null) {
            return;
        }

        addArg(args, file.getAbsolutePath());
    }

    private void addArgs(Map<String, ?> args) {
        args.forEach((label, value) -> {
            if (value instanceof String) {
                addArg(label, (String) value);
            } else if (value instanceof File) {
                addArg(label, (File) value);
            }
        });
    }

}
