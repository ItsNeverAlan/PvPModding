package net.pvp.mixins.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pvp.PvPModding;
import net.pvp.util.EntityUtil;
import net.pvp.util.PlayerUtil;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;

@Mixin(ItemSword.class)
public abstract class MixinItemSword extends Item {

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (!PvPModding.isEnabled()) {
            return super.onItemRightClick(worldIn,playerIn, handIn);
        }

        if (handIn == EnumHand.MAIN_HAND) {
            ItemStack itemStackreverse = playerIn.getHeldItem(EnumHand.OFF_HAND);

            if (!itemStackreverse.isEmpty()) {
                ActionResult<ItemStack> actionResult = itemStackreverse.getItem().onItemRightClick(worldIn, playerIn, EnumHand.OFF_HAND);
                if (actionResult.getType() == EnumActionResult.SUCCESS) {
                    return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
                }
            }
        }

        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        if (!PvPModding.isEnabled()) {
            return super.getMaxItemUseDuration(stack);
        }
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        if (!PvPModding.isEnabled()) {
            return super.getItemUseAction(stack);
        }
        return EnumAction.BLOCK;
    }

}
