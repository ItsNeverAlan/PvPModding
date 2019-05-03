package net.pvp.mixins;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.command.CommandGameRule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.pvp.PvPModding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

    @Shadow public int maxHurtResistantTime;
    @Shadow public int hurtTime;
    @Shadow public int maxHurtTime;
    @Shadow protected int scoreValue;
    @Shadow public float attackedAtYaw;
    @Shadow public float limbSwingAmount;
    @Shadow public boolean potionsNeedUpdate;
    @Shadow public boolean dead;
    @Shadow public CombatTracker combatTracker;
    @Shadow @Nullable
    public EntityLivingBase revengeTarget;
    @Shadow protected AbstractAttributeMap attributeMap;
    @Shadow protected int idleTime;
    @Shadow protected int recentlyHit;
    @Shadow protected float lastDamage;
    @Shadow @Nullable protected EntityPlayer attackingPlayer;
    @Shadow protected ItemStack activeItemStack;
    @Shadow private DamageSource lastDamageSource;
    @Shadow private long lastDamageStamp;
    @Nullable private ItemStack activeItemStackCopy;

    @Shadow public abstract int getItemInUseCount();
    @Shadow public abstract void resetActiveHand();

    @Shadow protected int activeItemStackUseCount;

    // Empty body so that we can call super() in MixinEntityPlayer
    @Shadow public void stopActiveHand() {
    }

    @Shadow
    protected abstract float applyArmorCalculations(DamageSource source, float damage);

    @Shadow
    protected abstract float applyPotionDamageCalculations(DamageSource source, float damage);

    @Shadow protected abstract void markVelocityChanged();
    @Shadow protected abstract SoundEvent getDeathSound();
    @Shadow protected abstract float getSoundVolume();
    @Shadow protected abstract float getSoundPitch();
    @Shadow protected abstract SoundEvent getHurtSound(DamageSource cause);
    @Shadow public abstract void setHealth(float health);
    @Shadow public abstract void addPotionEffect(net.minecraft.potion.PotionEffect potionEffect);
    @Shadow protected abstract void markPotionsDirty();
    @Shadow public abstract void clearActivePotions();
    @Shadow public abstract void setLastAttackedEntity(net.minecraft.entity.Entity entity);
    @Shadow public abstract boolean isPotionActive(Potion potion);
    @Shadow public abstract float getHealth();
    @Shadow public abstract float getMaxHealth();
    @Shadow public abstract float getRotationYawHead();
    @Shadow public abstract void setRotationYawHead(float rotation);
    @Shadow public abstract Collection getActivePotionEffects();
    @Shadow @Nullable public abstract EntityLivingBase getLastAttackedEntity();
    @Shadow public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);
    @Shadow public abstract ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn);
    @Shadow protected abstract void applyEntityAttributes();
    @Shadow protected abstract void playHurtSound(net.minecraft.util.DamageSource p_184581_1_);
    @Shadow protected abstract void damageShield(float p_184590_1_);
    @Shadow public abstract void setActiveHand(EnumHand hand);
    @Shadow public abstract ItemStack getHeldItem(EnumHand hand);
    @Shadow public abstract void setHeldItem(EnumHand hand, @Nullable ItemStack stack);
    @Shadow public abstract ItemStack getHeldItemMainhand();
    @Shadow public abstract boolean isHandActive();
    @Shadow protected abstract void onDeathUpdate();
    @Shadow public abstract void knockBack(net.minecraft.entity.Entity entityIn, float p_70653_2_, double p_70653_3_, double p_70653_5_);
    @Shadow public abstract void shadow$setRevengeTarget(EntityLivingBase livingBase);
    @Shadow public abstract void setAbsorptionAmount(float amount);
    @Shadow public abstract float getAbsorptionAmount();
    @Shadow public abstract CombatTracker getCombatTracker();
    @Shadow public abstract void setSprinting(boolean sprinting);
    @Shadow public abstract boolean isOnLadder();
    @Shadow @Nullable public abstract EntityLivingBase getAttackingEntity();
    @Shadow protected abstract void damageEntity(DamageSource damageSrc, float damageAmount);
    @Shadow protected abstract void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source);
    @Shadow protected abstract boolean canDropLoot();
    @Shadow public abstract Random getRNG();
    @Shadow protected abstract void blockUsingShield(EntityLivingBase p_190629_1_);
    @Shadow public abstract boolean canBlockDamageSource(DamageSource p_184583_1_);
    @Shadow private boolean checkTotemDeathProtection(DamageSource p_190628_1_) {
        return false; // SHADOWED
    }
    @Shadow public abstract AbstractAttributeMap getAttributeMap();
    @Shadow public void onKillCommand() {
        // Non-abstract for MixinEntityArmorStand
    }
    @Shadow protected abstract int getExperiencePoints(EntityPlayer attackingPlayer);

    @Shadow @Nullable public abstract EntityLivingBase getRevengeTarget();

    @Shadow public int deathTime;

    @Shadow public abstract void heal(float healAmount);

    @Shadow public abstract void setRevengeTarget(@Nullable EntityLivingBase livingBase);
    @Shadow public abstract void onDeath(DamageSource cause);
    @Shadow public float randomUnused1;

    @Shadow public abstract EnumHand getActiveHand();

    @Shadow public abstract ItemStack getActiveItemStack();

    @Shadow protected double interpTargetPitch;
    private int deathEventsPosted;

    /**
     * @author LeeGod
     * @reason remove shield blocking
     */
    @Overwrite
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (this.world.isRemote)
        {
            return false;
        }
        else
        {
            this.idleTime = 0;

            if (this.getHealth() <= 0.0F)
            {
                return false;
            }
            else if (source.isFireDamage() && this.isPotionActive(MobEffects.FIRE_RESISTANCE))
            {
                return false;
            }
            else
            {
                float f = amount;

                if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && !this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty())
                {
                    this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).damageItem((int)(amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), (EntityLivingBase) (Object) this);
                    amount *= 0.75F;
                }

                boolean flag = false;

                if (amount > 0.0F && this.canBlockDamageSource(source))
                {
                    this.damageShield(amount);
                    amount = 0.0F;

                    if (!source.isProjectile())
                    {
                        Entity entity = source.getImmediateSource();

                        if (entity instanceof EntityLivingBase)
                        {
                            this.blockUsingShield((EntityLivingBase)entity);
                        }
                    }

                    flag = true;
                }

                this.limbSwingAmount = 1.5F;
                boolean flag1 = true;

                if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F)
                {
                    if (amount <= this.lastDamage)
                    {
                        return false;
                    }

                    this.damageEntity(source, amount - this.lastDamage);
                    this.lastDamage = amount;
                    flag1 = false;
                }
                else
                {
                    this.lastDamage = amount;
                    this.hurtResistantTime = this.maxHurtResistantTime;
                    this.damageEntity(source, amount);
                    this.maxHurtTime = 10;
                    this.hurtTime = this.maxHurtTime;
                }

                this.attackedAtYaw = 0.0F;
                Entity entity1 = source.getTrueSource();

                if (entity1 != null)
                {
                    if (entity1 instanceof EntityLivingBase)
                    {
                        this.setRevengeTarget((EntityLivingBase)entity1);
                    }

                    if (entity1 instanceof EntityPlayer)
                    {
                        this.recentlyHit = 100;
                        this.attackingPlayer = (EntityPlayer)entity1;
                    }
                    else if (entity1 instanceof EntityWolf)
                    {
                        EntityWolf entitywolf = (EntityWolf)entity1;

                        if (entitywolf.isTamed())
                        {
                            this.recentlyHit = 100;
                            this.attackingPlayer = null;
                        }
                    }
                }

                if (flag1)
                {
                    if (flag)
                    {
                        this.world.setEntityState((EntityLivingBase) (Object) this, (byte)29);
                    }
                    else if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage())
                    {
                        this.world.setEntityState((EntityLivingBase) (Object) this, (byte)33);
                    }
                    else
                    {
                        byte b0;

                        if (source == DamageSource.DROWN)
                        {
                            b0 = 36;
                        }
                        else if (source.isFireDamage())
                        {
                            b0 = 37;
                        }
                        else
                        {
                            b0 = 2;
                        }

                        this.world.setEntityState((EntityLivingBase) (Object) this, b0);
                    }

                    if (source != DamageSource.DROWN && (!flag || amount > 0.0F))
                    {
                        this.markVelocityChanged();
                    }

                    if (entity1 != null)
                    {
                        double d1 = entity1.posX - this.posX;
                        double d0;

                        for (d0 = entity1.posZ - this.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
                        {
                            d1 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.attackedAtYaw = (float)(MathHelper.atan2(d0, d1) * (180D / Math.PI) - (double)this.rotationYaw);
                        this.knockBack(entity1, 0.4F, d1, d0);
                    }
                    else
                    {
                        this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
                    }
                }

                if (this.getHealth() <= 0.0F)
                {
                    if (!this.checkTotemDeathProtection(source))
                    {
                        SoundEvent soundevent = this.getDeathSound();

                        if (flag1 && soundevent != null)
                        {
                            this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
                        }

                        this.onDeath(source);
                    }
                }
                else if (flag1)
                {
                    this.playHurtSound(source);
                }

                boolean flag2 = !flag || amount > 0.0F;

                if (flag2)
                {
                    this.lastDamageSource = source;
                    this.lastDamageStamp = this.world.getTotalWorldTime();
                }

                if ((Object) this instanceof EntityPlayerMP)
                {
                    CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((EntityPlayerMP) (Object) this, source, f, amount, flag);
                }

                if (entity1 instanceof EntityPlayerMP)
                {
                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((EntityPlayerMP)entity1, (EntityLivingBase) (Object) this, source, f, amount, flag);
                }

                return flag2;
            }
        }
    }

    /**
     * @author LeeGod
     * @reason shield
     * @return only for shield
     */
    @Overwrite
    public boolean isActiveItemStackBlocking()
    {
        if (this.isHandActive() && !this.activeItemStack.isEmpty() && !(this.activeItemStack.getItem() instanceof ItemSword))
        {
            Item item = this.activeItemStack.getItem();

            if (item.getItemUseAction(this.activeItemStack) != EnumAction.BLOCK)
            {
                return false;
            }
            else
            {
                return item.getMaxItemUseDuration(this.activeItemStack) - this.activeItemStackUseCount >= 5;
            }
        }
        else
        {
            return false;
        }
    }

}
