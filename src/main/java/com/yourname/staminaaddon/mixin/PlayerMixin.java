package com.yourname.staminaaddon.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "canSprint", at = @At("HEAD"), cancellable = true)
    private void alwaysAllowSprint(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
