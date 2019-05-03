package net.pvp.util;

import net.minecraft.util.EnumHand;

public class PlayerUtil {

    public static EnumHand getReverseHand(EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND) {
            return EnumHand.OFF_HAND;
        }
        return EnumHand.MAIN_HAND;
    }

}
