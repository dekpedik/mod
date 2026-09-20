package com.addon.stamina.event;

import com.addon.stamina.StaminaAddon;
import com.addon.stamina.logic.StaminaDebtManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StaminaAddon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StaminaTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        float staminaMultiplier = StaminaDebtManager.getMaxStaminaMultiplier(player);

        // Применение модификатора стамины к атрибутам / капам Immersive Stamina
        // В зависимости от структуры API Immersive Stamina, значение выставляется через AttributeInstance
        // или вызов кастомного Capability стамины.
    }
}
