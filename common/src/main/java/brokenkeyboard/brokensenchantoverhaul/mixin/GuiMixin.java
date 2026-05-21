package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.HeartType.class)
public class GuiMixin {

    @Inject(method = "forPlayer", at = @At("RETURN"), cancellable = true)
    private static void changeHeartColors(Player player, CallbackInfoReturnable<Gui.HeartType> cir) {
        if (Services.PLATFORM.getBarrierAmount(player) > 0) {
            cir.setReturnValue(Gui.HeartType.valueOf("BROKENSENCHANTOVERHAUL_BARRIER"));
        }
    }
}
