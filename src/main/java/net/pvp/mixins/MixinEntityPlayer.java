package net.pvp.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameRules;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.pvp.PvPModding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

@Mixin({EntityPlayer.class})
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {

    @Shadow
    public Container inventoryContainer;
    @Shadow
    public Container openContainer;
    @Shadow
    public int experienceLevel;
    @Shadow
    public int experienceTotal;
    @Shadow
    public float experience;
    @Shadow
    public PlayerCapabilities capabilities;
    @Shadow
    public InventoryPlayer inventory;
    @Shadow
    private BlockPos spawnPos;
    @Shadow
    private BlockPos bedLocation;
    @Shadow
    protected FoodStats foodStats;
    @Shadow
    public InventoryEnderChest enderChest;

    @Shadow
    public abstract boolean canOpen(LockCode code);

    @Shadow
    public abstract boolean isPlayerSleeping();

    @Shadow
    public abstract boolean isSpectator();

    @Shadow
    public abstract int xpBarCap();

    @Shadow
    public abstract float getCooledAttackStrength(float adjustTicks);

    @Shadow
    public abstract float getAIMoveSpeed();

    @Shadow
    public abstract void onCriticalHit(net.minecraft.entity.Entity entityHit);

    @Shadow
    public abstract void onEnchantmentCritical(net.minecraft.entity.Entity entityHit); // onEnchantmentCritical

    @Shadow
    public abstract void addExhaustion(float p_71020_1_);

    @Shadow
    public abstract void addStat(@Nullable StatBase stat, int amount);

    @Shadow
    public abstract void addStat(StatBase stat);

    @Shadow
    public abstract void resetCooldown();

    @Shadow
    public abstract void spawnSweepParticles(); //spawnSweepParticles()

    @Shadow
    public abstract void takeStat(StatBase stat);

    @Shadow
    protected abstract void destroyVanishingCursedItems(); // Filter vanishing curse enchanted items

    @Shadow
    public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
    }

    ;

    @Shadow
    public abstract EntityItem dropItem(boolean dropAll);

    @Shadow
    public abstract FoodStats getFoodStats();

    @Shadow
    public abstract GameProfile getGameProfile();

    @Shadow
    public abstract Scoreboard getWorldScoreboard();

    @Shadow
    public abstract String getName();

    @Shadow
    @Nullable
    public abstract Team getTeam();

    @Shadow
    public abstract void addExperienceLevel(int levels);

    @Shadow
    public abstract void addScore(int scoreIn);

    @Shadow
    public abstract CooldownTracker shadow$getCooldownTracker();

    @Shadow
    protected abstract void spawnShoulderEntities();

    @Shadow
    public abstract boolean isCreative();

    private EntityPlayer thePlayer = (EntityPlayer) (Object) this;

    /**
     * @author LeeGod
     * @reason Remove cooldown
     **/
    @Inject(method = "resetCooldown", cancellable = true, at = @At("HEAD"))
    public void resetCooldown(CallbackInfo ci) {
        if (PvPModding.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "spawnSweepParticles", cancellable = true, at = @At("HEAD"))
    public void spawnSweepParticles(CallbackInfo ci) {
        if (PvPModding.isEnabled()) {
            ci.cancel();
        }
    }

    /**
     * @author LeeGod
     * @reason Remove sound
     **/
    @Overwrite
    public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
        if (targetEntity.canBeAttackedWithItem()) {
            if (!targetEntity.hitByEntity(thePlayer)) {
                float f = (float) thePlayer.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                float f1;

                if (targetEntity instanceof EntityLivingBase) {
                    f1 = EnchantmentHelper.getModifierForCreature(thePlayer.getHeldItemMainhand(), ((EntityLivingBase) targetEntity).getCreatureAttribute());
                } else {
                    f1 = EnchantmentHelper.getModifierForCreature(thePlayer.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED);
                }

                float f2 = 1.0f;

                if (!PvPModding.isEnabled()) {
                    f2 = thePlayer.getCooledAttackStrength(0.5F);
                    f = f * (0.2F + f2 * f2 * 0.8F);
                    f1 = f1 * f2;
                    this.resetCooldown();
                }

                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = f2 > 0.9f;
                    boolean flag1 = false;
                    int i = 0;
                    i = i + EnchantmentHelper.getKnockbackModifier(thePlayer);

                    if (thePlayer.isSprinting() && flag) {
                        thePlayer.world.playSound((EntityPlayer) null, thePlayer.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
                        ++i;
                        flag1 = true;
                    }

                    boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(MobEffects.BLINDNESS) && !this.isRiding() && targetEntity instanceof EntityLivingBase;
                    flag2 = flag2 && !this.isSprinting();

                    if (flag2) {
                        f *= 1.5F;
                    }

                    f = f + f1;
                    boolean flag3 = false;
                    double d0 = (double) (this.distanceWalkedModified - this.prevDistanceWalkedModified);

                    if (!PvPModding.isEnabled() && flag && !flag2 && !flag1 && this.onGround && d0 < (double) this.getAIMoveSpeed()) {
                        ItemStack itemstack = this.getHeldItem(EnumHand.MAIN_HAND);

                        if (itemstack.getItem() instanceof ItemSword) {
                            flag3 = true;
                        }
                    }

                    float f4 = 0.0F;
                    boolean flag4 = false;
                    int j = EnchantmentHelper.getFireAspectModifier(thePlayer);

                    if (targetEntity instanceof EntityLivingBase) {
                        f4 = ((EntityLivingBase) targetEntity).getHealth();

                        if (j > 0 && !targetEntity.isBurning()) {
                            flag4 = true;
                            targetEntity.setFire(1);
                        }
                    }

                    double d1 = targetEntity.motionX;
                    double d2 = targetEntity.motionY;
                    double d3 = targetEntity.motionZ;
                    boolean flag5 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(thePlayer), f);

                    if (flag5) {
                        if (i > 0) {
                            if (targetEntity instanceof EntityLivingBase) {
                                ((EntityLivingBase) targetEntity).knockBack(thePlayer, (float) i * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                            } else {
                                targetEntity.addVelocity((double) (-MathHelper.sin(this.rotationYaw * 0.017453292F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.rotationYaw * 0.017453292F) * (float) i * 0.5F));
                            }

                            this.motionX *= 0.6D;
                            this.motionZ *= 0.6D;
                        }

                        if (flag3 && !PvPModding.isEnabled()) {
                            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(thePlayer) * f;

                            for (EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, targetEntity.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
                                if (entitylivingbase != thePlayer && entitylivingbase != targetEntity && !this.isOnSameTeam(entitylivingbase) && this.getDistanceSq(entitylivingbase) < 9.0D) {
                                    entitylivingbase.knockBack(thePlayer, 0.4F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                                    entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(thePlayer), f3);
                                }
                            }

                            this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
                            this.spawnSweepParticles();

                        }

                        if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                            ((EntityPlayerMP) targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.motionX = d1;
                            targetEntity.motionY = d2;
                            targetEntity.motionZ = d3;
                        }

                        if (flag2) {
                            if (!PvPModding.isEnabled())
                                this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
                            this.onCriticalHit(targetEntity);
                        }

                        if (!flag2 && !flag3 && !PvPModding.isEnabled()) {
                            if (flag) {
                                this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                                this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if (f1 > 0.0F) {
                            this.onEnchantmentCritical(targetEntity);
                        }

                        this.setLastAttackedEntity(targetEntity);

                        if (targetEntity instanceof EntityLivingBase) {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase) targetEntity, thePlayer);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(thePlayer, targetEntity);
                        ItemStack itemstack1 = this.getHeldItemMainhand();
                        Entity entity = targetEntity;

                        if (targetEntity instanceof MultiPartEntityPart) {
                            IEntityMultiPart ientitymultipart = ((MultiPartEntityPart) targetEntity).parent;

                            if (ientitymultipart instanceof EntityLivingBase) {
                                entity = (EntityLivingBase) ientitymultipart;
                            }
                        }

                        if (!itemstack1.isEmpty() && entity instanceof EntityLivingBase) {
                            itemstack1.hitEntity((EntityLivingBase) entity, thePlayer);

                            if (itemstack1.isEmpty()) {
                                this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (targetEntity instanceof EntityLivingBase) {
                            float f5 = f4 - ((EntityLivingBase) targetEntity).getHealth();
                            this.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0F));

                            if (j > 0) {
                                targetEntity.setFire(j * 4);
                            }

                            if (this.world instanceof WorldServer && f5 > 2.0F && !PvPModding.isEnabled()) {
                                int k = (int) ((double) f5 * 0.5D);
                                ((WorldServer) this.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double) (targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        this.addExhaustion(0.1F);
                    } else {
                        if (!PvPModding.isEnabled())
                            this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);

                        if (flag4) {
                            targetEntity.extinguish();
                        }
                    }
                    if (thePlayer instanceof EntityPlayerMP) {
                        thePlayer.sendMessage(new TextComponentString("hi " + f));
                    }
                }
            }
        }
    }

}
