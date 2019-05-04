package net.pvp.mixins;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.block.material.Material;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.*;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraftforge.fml.relauncher.FMLSecurityManager;
import net.pvp.PvPModding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.net.Proxy;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.FutureTask;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow private File fileResourcepacks;
    @Shadow private PropertyMap twitchDetails;
    @Shadow private PropertyMap profileProperties;
    @Shadow private ServerData currentServerData;
    @Shadow private TextureManager renderEngine;
    @Shadow private static Minecraft instance;
    @Shadow private DataFixer dataFixer;
    @Shadow public PlayerControllerMP playerController;
    @Shadow private boolean fullscreen;
    @Shadow private boolean enableGLErrorChecking;
    @Shadow private boolean hasCrashed;
    @Shadow private CrashReport crashReporter;
    @Shadow public int displayWidth;
    @Shadow public int displayHeight;
    @Shadow private boolean connectedToRealms;
    @Shadow private Timer timer;
    @Shadow private Snooper usageSnooper;
    @Shadow public WorldClient world;
    @Shadow public RenderGlobal renderGlobal;
    @Shadow private RenderManager renderManager;
    @Shadow private RenderItem renderItem;
    @Shadow private ItemRenderer itemRenderer;
    @Shadow public EntityPlayerSP player;
    @Shadow @Nullable
    private Entity renderViewEntity;
    @Shadow public Entity pointedEntity;
    @Shadow public ParticleManager effectRenderer;
    @Shadow private SearchTreeManager searchTreeManager;
    @Shadow private Session session;
    @Shadow private boolean isGamePaused;
    @Shadow private float renderPartialTicksPaused;
    @Shadow public FontRenderer fontRenderer;
    @Shadow public FontRenderer standardGalacticFontRenderer;
    @Shadow @Nullable
    public GuiScreen currentScreen;
    @Shadow public LoadingScreenRenderer loadingScreen;
    @Shadow public EntityRenderer entityRenderer;
    @Shadow public DebugRenderer debugRenderer;
    @Shadow private int leftClickCounter;
    @Shadow private int tempDisplayWidth;
    @Shadow private int tempDisplayHeight;
    @Shadow @Nullable
    private IntegratedServer integratedServer;
    @Shadow public GuiIngame ingameGUI;
    @Shadow public boolean skipRenderWorld;
    @Shadow public RayTraceResult objectMouseOver;
    @Shadow public GameSettings gameSettings;
    @Shadow public CreativeSettings creativeSettings;
    @Shadow  public MouseHelper mouseHelper;
    @Shadow private File fileAssets;
    @Shadow private String launchedVersion;
    @Shadow private String versionType;
    @Shadow private Proxy proxy;
    @Shadow private ISaveFormat saveLoader;
    @Shadow private int rightClickDelayTimer;
    @Shadow private String serverName;
    @Shadow private int serverPort;
    @Shadow public boolean inGameHasFocus;
    @Shadow long systemTime;
    @Shadow private int joinPlayerCounter;
    @Shadow public FrameTimer frameTimer;
    @Shadow long startNanoTime;
    @Shadow private boolean jvm64bit;
    @Shadow private boolean isDemo;
    @Shadow private boolean integratedServerIsRunning;
    @Shadow private long debugCrashKeyPressTime;
    @Shadow private List<IResourcePack> defaultResourcePacks;
    @Shadow private BlockColors blockColors;
    @Shadow private ItemColors itemColors;
    @Shadow private TextureMap textureMapBlocks;
    @Shadow private ResourceLocation mojangLogo;
    @Shadow private MinecraftSessionService sessionService;
    @Shadow private SkinManager skinManager;
    @Shadow private Queue<FutureTask<? >> scheduledTasks;
    @Shadow private ModelManager modelManager;
    @Shadow private BlockRendererDispatcher blockRenderDispatcher;
    @Shadow private GuiToast toastGui;
    @Shadow boolean running;
    @Shadow public String debug;
    @Shadow public boolean renderChunksMany;
    @Shadow private long debugUpdateTime;
    @Shadow private int fpsCounter;
    @Shadow private boolean actionKeyF3;
    @Shadow private Tutorial tutorial;
    @Shadow long prevFrameTime;
    @Shadow  private String debugProfilerName;

    /**
     * @author LeeGod
     * @reason 1.7 swing animation
     */
    @Overwrite
    private void clickMouse()
    {
        FMLSecurityManager
        if (this.leftClickCounter <= 0)
        {
            boolean modOn = PvPModding.isEnabled();

            if (modOn) this.player.swingArm(EnumHand.MAIN_HAND);

            if (this.objectMouseOver == null)
            {
                PvPModding.LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");

                if (this.playerController.isNotCreative())
                {
                    this.leftClickCounter = 10;
                }
            }
            else if (!this.player.isRowingBoat())
            {
                switch (this.objectMouseOver.typeOfHit)
                {
                    case ENTITY:
                        this.playerController.attackEntity(this.player, this.objectMouseOver.entityHit);
                        break;
                    case BLOCK:
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();

                        if (this.world.getBlockState(blockpos).getMaterial() != Material.AIR)
                        {
                            this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                            break;
                        }

                    case MISS:

                        if (this.playerController.isNotCreative())
                        {
                            this.leftClickCounter = 10;
                        }

                        this.player.resetCooldown();
                }

                if (!modOn) {
                    this.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
        }
    }

    /**
     * @author LeeGod
     * @reason 1.7 swing animation
     */
    @Overwrite
    private void sendClickBlockToController(boolean leftClick)
    {
        if (!leftClick)
        {
            this.leftClickCounter = 0;
        }

        if (this.leftClickCounter <= 0 /*&& !this.player.isHandActive()*/)
        {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();

                if (this.world.getBlockState(blockpos).getMaterial() != Material.AIR && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit))
                {
                    this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
                    this.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
            else
            {
                this.playerController.resetBlockRemoving();
            }
        }
    }

}
