package com.yourname.staminaaddon.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.mcreator.kerilom.immersivestamina.client.screens.StaminaOverlay;
import net.mcreator.kerilom.immersivestamina.network.ImmersiveStaminaModVariables;
import net.mcreator.kerilom.immersivestamina.configuration.ConfigConfiguration;
import net.mcreator.kerilom.immersivestamina.procedures.HideStaminaProcedureHUDProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StaminaOverlay.class, remap = false)
public abstract class StaminaOverlayMixin {

    @Inject(method = "eventHandler", at = @At("HEAD"), cancellable = true)
    private static void customHUD(RenderGuiEvent.Pre event, CallbackInfo ci) {
        ci.cancel(); // Блокируем оригинальную отрисовку

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator() || player.isCreative()) return;

        // Проверка: включен ли HUD в конфигах Immersive Stamina
        if (!HideStaminaProcedureHUDProcedure.execute(player)) return;

        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        player.getCapability(ImmersiveStaminaModVariables.PLAYER_VARIABLES).ifPresent(cap -> {
            double maxStamina = ConfigConfiguration.MAX_STAMINA.get();
            double currentStamina = cap.stamina;
            int foodLevel = player.getFoodData().getFoodLevel();

            // 1. Отрисовка основной шкалы стамины
            ResourceLocation spriteBase = new ResourceLocation("immersive_stamina:textures/screens/stamina.png");
            double staminaPct = Mth.clamp(currentStamina / maxStamina, 0.0, 1.0);
            int baseFrame = (int) Math.round(staminaPct * 20.0); // 21 кадр (0 до 20)
            event.getGuiGraphics().blit(spriteBase, width / 2 + 93, height - 40, 0, baseFrame * 16, 16, 16, 16, 336);

            // 2. Отрисовка белой обводки (Glow) от сытости
            if (foodLevel > 0) {
                ResourceLocation spriteGlow = new ResourceLocation("immersive_stamina:textures/screens/glow.png");
                double foodPct = Mth.clamp(foodLevel / 20.0, 0.0, 1.0);
                int glowFrame = (int) Math.round(foodPct * 7.0); // 8 кадров (0 до 7)
                event.getGuiGraphics().blit(spriteGlow, width / 2 + 93, height - 40, 0, glowFrame * 16, 16, 16, 16, 128);
            }
        });
    }
}
