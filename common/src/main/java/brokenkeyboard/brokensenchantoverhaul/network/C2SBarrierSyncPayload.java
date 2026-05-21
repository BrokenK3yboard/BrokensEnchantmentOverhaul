package brokenkeyboard.brokensenchantoverhaul.network;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SBarrierSyncPayload(int entityID) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<C2SBarrierSyncPayload> TYPE = new CustomPacketPayload.Type<>(ModRegistry.location("c2s_barrier_amount"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SBarrierSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, C2SBarrierSyncPayload::entityID,
            C2SBarrierSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
