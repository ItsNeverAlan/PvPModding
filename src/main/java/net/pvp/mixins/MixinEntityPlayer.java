package net.pvp.mixins;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {

    /**
     * @author LeeGod
     * @reason Remove cooldown
     **/
    @Overwrite
    public void resetCooldown() {
    }

}
