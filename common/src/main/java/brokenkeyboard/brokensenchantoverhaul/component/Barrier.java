package brokenkeyboard.brokensenchantoverhaul.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Barrier(int timestamp, int charges) {

    public static final Codec<Barrier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("timestamp").forGetter(Barrier::timestamp),
            Codec.INT.fieldOf("charges").forGetter(Barrier::charges)
    ).apply(instance, Barrier::new));

    public static final StreamCodec<ByteBuf, Barrier> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Barrier::timestamp,
            ByteBufCodecs.INT, Barrier::charges,
            Barrier::new
    );
}
