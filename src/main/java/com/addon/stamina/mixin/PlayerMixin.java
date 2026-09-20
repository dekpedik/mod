package com.addon.stamina.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    /**
     * Переопределяет проверку возможности поедания пищи.
     * Возвращает true всегда, разрешая поедание при полной сытости.
     */
    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
    private void allowEatingAtFullHunger(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
