package brokenkeyboard.brokensenchantoverhaul.network;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record S2CBarrierSyncPayload(int entityID, int amount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CBarrierSyncPayload> TYPE = new CustomPacketPayload.Type<>(ModRegistry.location("s2c_barrier_amount"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CBarrierSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, S2CBarrierSyncPayload::entityID,
            ByteBufCodecs.INT, S2CBarrierSyncPayload::amount,
            S2CBarrierSyncPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
