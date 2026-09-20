package com.yourname.staminaaddon.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.mcreator.kerilom.immersivestamina.procedures.RegenTickProcedure;
import net.mcreator.kerilom.immersivestamina.network.ImmersiveStaminaModVariables;
import net.mcreator.kerilom.immersivestamina.configuration.ConfigConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.yourname.staminaaddon.IStaminaDebt;

@Mixin(value = RegenTickProcedure.class, remap = false)
public abstract class RegenTickProcedureMixin {
    
    @Inject(method = "execute", at = @At("TAIL"))
    private static void applyDynamicStaminaCap(Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            int debt = 0;
            if (player.getFoodData() instanceof IStaminaDebt isd) {
                debt = isd.immersivestamina$getDebt();
            }
            
            if (debt > 0) {
                entity.getCapability(ImmersiveStaminaModVariables.PLAYER_VARIABLES).ifPresent(cap -> {
                    double maxVanilla = ConfigConfiguration.MAX_STAMINA.get();
                    // Вычисляем потолок: отнимаем 5% за каждую единицу долга
                    double allowedMax = maxVanilla * (1.0 - (debt * 0.05));
                    
                    if (cap.stamina > allowedMax) {
                        cap.stamina = allowedMax;
                        cap.markSyncDirty(); // Отправляем пакет синхронизации на клиент
                    }
                });
            }
        }
    }
}
