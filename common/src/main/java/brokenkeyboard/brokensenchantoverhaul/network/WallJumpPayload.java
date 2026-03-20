package brokenkeyboard.brokensenchantoverhaul.network;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record WallJumpPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<WallJumpPayload> TYPE = new CustomPacketPayload.Type<>(ModRegistry.location("wall_jump"));
    public static final StreamCodec<ByteBuf, WallJumpPayload> STREAM_CODEC = StreamCodec.unit(new WallJumpPayload());

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
