package net.pvp.util;

import net.minecraft.entity.EntityLivingBase;

import java.lang.reflect.Field;

public class EntityUtil {

    private static Field usecountField;

    static {
        try {
            usecountField = EntityLivingBase.class.getDeclaredField("activeItemStackUseCount");
            usecountField.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setUseCount(EntityLivingBase entityLivingBase, int count) {
        try {
            usecountField.set(entityLivingBase, count);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
