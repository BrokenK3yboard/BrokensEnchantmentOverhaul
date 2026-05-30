package brokenkeyboard.brokensenchantoverhaul.network;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record S2CDetectedBlocks(int entityID, boolean nearBlocks) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CDetectedBlocks> TYPE = new CustomPacketPayload.Type<>(ModRegistry.location("s2c_detector_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CDetectedBlocks> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, S2CDetectedBlocks::entityID,
            ByteBufCodecs.BOOL, S2CDetectedBlocks::nearBlocks,
            S2CDetectedBlocks::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
