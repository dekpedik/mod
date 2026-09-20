package com.addon.stamina.mixin;

import com.addon.stamina.logic.StaminaDebtManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Использование @Pseudo позволяет безопасно миксиниться в класс интерфейса 
 * Immersive Stamina, даже если класс загружается динамически.
 */
@Pseudo
@Mixin(targets = "com.immersivestamina.client.gui.StaminaHudOverlay", remap = false)
public abstract class ImmersiveStaminaHudMixin {

    @Inject(method = "renderBorder", at = @At("HEAD"), cancellable = true)
    private void onRenderBorder(GuiGraphics guiGraphics, float partialTicks, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        int foodLevel = player.getFoodData().getFoodLevel();
        float currentStamina = 0.0f; // Получение текущей стамины из капабилити мода

        // 1. При долге стамины или 0 сытости — полностью отменяем рендер белой обводки
        if (foodLevel <= 0 || StaminaDebtManager.isInStaminaDebt(player, currentStamina)) {
            ci.cancel();
            return;
        }

        // 2. Расчет альфа-канала/прозрачности обводки от 0.0 (при 0) до 1.0 (при 20 сытости)
        float borderAlpha = foodLevel / 20.0f;

        // Если сытость меньше 20, скрываем стандартный вызов и рендерим с прозрачностью
        if (borderAlpha < 1.0f) {
            ci.cancel();
            renderCustomBorder(guiGraphics, borderAlpha);
        }
    }

    private void renderCustomBorder(GuiGraphics guiGraphics, float alpha) {
        // Рендер текстуры белой обводки Immersive Stamina с урезанной прозрачностью (alpha)
    }
}
