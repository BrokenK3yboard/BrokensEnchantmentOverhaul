package brokenkeyboard.brokensenchantoverhaul.network;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SWallJumpPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<C2SWallJumpPayload> TYPE = new CustomPacketPayload.Type<>(ModRegistry.location("wall_jump"));
    public static final StreamCodec<ByteBuf, C2SWallJumpPayload> STREAM_CODEC = StreamCodec.unit(new C2SWallJumpPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
