package net.pvp.mixins.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(World.class)
public class MixinWorld {

//    @Inject(method = {"playSound"}, cancellable = true, at = @At("HEAD"))
//    public void playSound(EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, CallbackInfo info) {
//        if (soundIn == SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP) {
//            info.cancel();
//        }
//    }
}
