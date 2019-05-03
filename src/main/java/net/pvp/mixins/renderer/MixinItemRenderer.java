package net.pvp.mixins.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.pvp.PvPModding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Shadow
    private void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) { }

    @Shadow @Final private Minecraft mc;
    @Shadow private ItemStack itemStackMainHand = ItemStack.EMPTY;
    @Shadow private ItemStack itemStackOffHand = ItemStack.EMPTY;
    @Shadow private float equippedProgressMainHand;
    @Shadow private float prevEquippedProgressMainHand;
    @Shadow private float equippedProgressOffHand;
    @Shadow private float prevEquippedProgressOffHand;
    @Shadow @Final private RenderManager renderManager;
    @Shadow @Final private RenderItem itemRenderer;

    @Shadow
    private void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide hand, float p_187465_3_, ItemStack stack)
    {}

    @Shadow
    private void renderMapFirstPerson(float p_187463_1_, float p_187463_2_, float p_187463_3_)
    {}

    @Shadow
    private void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack)
    {}

    @Shadow
    private void transformFirstPerson(EnumHandSide hand, float p_187453_2_)
    {}

    @Shadow
    private void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_)
    {}

    @Shadow
    public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded)
    {}

    /**
     * @author LeeGod
     * @reason block sword
     */
    @Overwrite
    public void renderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_)
    {
        boolean flag = hand == EnumHand.MAIN_HAND;
        EnumHandSide enumhandside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        GlStateManager.pushMatrix();

        if (stack.isEmpty())
        {
            if (flag && !player.isInvisible())
            {
                this.renderArmFirstPerson(p_187457_7_, p_187457_5_, enumhandside);
            }
        }
        else if (stack.getItem() == Items.FILLED_MAP)
        {
            if (flag && this.itemStackOffHand.isEmpty())
            {
                this.renderMapFirstPerson(p_187457_3_, p_187457_7_, p_187457_5_);
            }
            else
            {
                this.renderMapFirstPersonSide(p_187457_7_, enumhandside, p_187457_5_, stack);
            }
        }
        else
        {
            boolean flag1 = enumhandside == EnumHandSide.RIGHT;

            if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == hand)
            {
                int j = flag1 ? 1 : -1;

                switch (stack.getItemUseAction())
                {
                    case NONE:
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        break;
                    case EAT:
                    case DRINK:
                        this.transformEatFirstPerson(p_187457_2_, enumhandside, stack);
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        break;
                    case BLOCK:
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        if (PvPModding.isEnabled() && stack.getItem() instanceof ItemSword)
                            this.func_178103_d(enumhandside);
                        break;
                    case BOW:
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        GlStateManager.translate((float)j * -0.2785682F, 0.18344387F, 0.15731531F);
                        GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate((float)j * 35.3F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
                        float f5 = (float)stack.getMaxItemUseDuration() - ((float)this.mc.player.getItemInUseCount() - p_187457_2_ + 1.0F);
                        float f6 = f5 / 20.0F;
                        f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;

                        if (f6 > 1.0F)
                        {
                            f6 = 1.0F;
                        }

                        if (f6 > 0.1F)
                        {
                            float f7 = MathHelper.sin((f5 - 0.1F) * 1.3F);
                            float f3 = f6 - 0.1F;
                            float f4 = f7 * f3;
                            GlStateManager.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                        }

                        GlStateManager.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
                        GlStateManager.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
                        GlStateManager.rotate((float)j * 45.0F, 0.0F, -1.0F, 0.0F);
                }
            }
            else
            {
                float f = -0.4F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * (float)Math.PI);
                float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * ((float)Math.PI * 2F));
                float f2 = -0.2F * MathHelper.sin(p_187457_5_ * (float)Math.PI);
                int i = flag1 ? 1 : -1;
                GlStateManager.translate((float)i * f, f1, f2);
                this.transformSideFirstPerson(enumhandside, p_187457_7_);
                this.transformFirstPerson(enumhandside, p_187457_5_);
            }

            this.renderItemSide(player, stack, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
        }

        GlStateManager.popMatrix();
    }

    private void func_178103_d(EnumHandSide hand)
    {
        int i = hand == EnumHandSide.RIGHT ? 1 : -1;
        // angle x y z
        //GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.translate((float) i * -0.1F, 0.1F, 0.1F);
        GlStateManager.rotate((float) i * 40.0F, (float) i * 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) i * -80.0F, (float) i * 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) i * 60.0F, (float) i * -0.15F, 1.05F, 1.16F);
    }

    public float sqrt_float(float value)
    {
        return (float)Math.sqrt((double)value);
    }

}
