package com.addon.stamina.logic;

import net.minecraft.world.entity.player.Player;

public class StaminaDebtManager {

    /**
     * Рассчитывает коэфф. максимальной стамины от уровня сытости.
     * 20 сытости -> 1.0 (100%)
     * 5 сытости  -> 0.5 (50%)
     * 0 сытости  -> 0.0 (0%)
     */
    public static float getMaxStaminaMultiplier(Player player) {
        int foodLevel = player.getFoodData().getFoodLevel();
        
        if (foodLevel <= 0) {
            return 0.0f;
        }
        
        // Линейная интерполяция между 5 ед. (50%) и 20 ед. (100%), либо прямая пропорция
        if (foodLevel <= 5) {
            return (foodLevel / 5.0f) * 0.5f;
        } else {
            return 0.5f + ((foodLevel - 5) / 15.0f) * 0.5f;
        }
    }

    /**
     * Проверяет, находится ли игрок в состоянии "Долга стамины".
     */
    public static boolean isInStaminaDebt(Player player, float currentStamina) {
        return currentStamina < 0.0f || player.getFoodData().getFoodLevel() == 0;
    }
}
