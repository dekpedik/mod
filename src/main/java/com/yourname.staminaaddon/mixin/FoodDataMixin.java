package com.yourname.staminaaddon.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.yourname.staminaaddon.IStaminaDebt;

@Mixin(FoodData.class)
public abstract class FoodDataMixin implements IStaminaDebt {
    @Shadow private int foodLevel;
    @Shadow private float saturationLevel;
    @Shadow private float exhaustionLevel;
    @Shadow private int tickTimer;

    @Unique
    private int stamina_debt = 0;

    @Override
    public int immersivestamina$getDebt() { return this.stamina_debt; }

    @Override
    public void immersivestamina$setDebt(int debt) { this.stamina_debt = debt; }

    // Перехватываем истощение: если голод 0, уводим в долг
    @Inject(method = "tick", at = @At("HEAD"))
    private void handleStaminaDebt(Player player, CallbackInfo ci) {
        if (this.exhaustionLevel > 4.0F) {
            if (this.saturationLevel <= 0.0F && player.level().getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL) {
                if (this.foodLevel == 0 && this.stamina_debt < 10) {
                    this.stamina_debt++; // Накапливаем долг (макс 10, что равно штрафу в -50%)
                }
            }
        }
    }

    // Полностью отключаем урон от голодания (сброс таймера)
    @Inject(method = "tick", at = @At("TAIL"))
    private void preventStarvationDamage(Player player, CallbackInfo ci) {
        if (this.foodLevel <= 0) {
            this.tickTimer = 0; 
        }
    }

    // Перехватываем процесс еды: сначала гасим долг, остаток идет в ванильную сытость
    @ModifyVariable(method = "eat(IF)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int interceptFoodEating(int foodAmount) {
        if (this.stamina_debt > 0) {
            int payoff = Math.min(this.stamina_debt, foodAmount);
            this.stamina_debt -= payoff;
            return foodAmount - payoff; // Если съели 5 при долге 1, вернет 4.
        }
        return foodAmount;
    }

    // Сохранение долга при выходе из игры
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveDebt(CompoundTag nbt, CallbackInfo ci) {
        nbt.putInt("ImmersiveStaminaDebt", this.stamina_debt);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readDebt(CompoundTag nbt, CallbackInfo ci) {
        this.stamina_debt = nbt.getInt("ImmersiveStaminaDebt");
    }
}
