package net.pvp.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
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

    @Shadow public float randomUnused1;
    private int deathEventsPosted;

}
