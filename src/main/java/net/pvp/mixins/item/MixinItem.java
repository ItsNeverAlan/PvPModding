package net.pvp.mixins.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pvp.PvPModding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public abstract class MixinItem {

    @Shadow
    private String translationKey;

    @Shadow public abstract int getItemStackLimit();
    @Shadow public abstract String getTranslationKey();

    @Shadow
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return null;
    }

    @Shadow
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return null;
    }

}
