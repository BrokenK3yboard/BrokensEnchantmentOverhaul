package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.platform.Services;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketMixin {

    @Inject(method = "handleAddEntity(Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addEntity(Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.AFTER), require = 0)
    private void syncEntities(ClientboundAddEntityPacket packet, CallbackInfo ci, @Local Entity entity) {
        if (entity.level().isClientSide()) {
            Services.PLATFORM.C2SBarrierSync(entity);
        }
    }
}