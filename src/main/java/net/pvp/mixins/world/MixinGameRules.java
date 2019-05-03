package net.pvp.mixins.world;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRules.class)
public abstract class MixinGameRules {

    @Shadow
    public abstract void addGameRule(String key, String value, GameRules.ValueType type);

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo info) {
        this.addGameRule("oldCombatMechanics", "true", GameRules.ValueType.BOOLEAN_VALUE);
    }

}
