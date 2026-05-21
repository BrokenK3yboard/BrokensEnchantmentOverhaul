package brokenkeyboard.brokensenchantoverhaul.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.Objects;

public record DamageTypeResist(String damageType, int stacks) {

    public static final Codec<DamageTypeResist> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("damage_type").forGetter(DamageTypeResist::damageType),
            Codec.INT.fieldOf("charges").forGetter(DamageTypeResist::stacks)
    ).apply(instance, DamageTypeResist::new));

    public static final StreamCodec<ByteBuf, DamageTypeResist> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DamageTypeResist::damageType,
            ByteBufCodecs.INT, DamageTypeResist::stacks,
            DamageTypeResist::new
    );

    public static String getResistanceType(DamageSource source) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return "PROJECTILE";
        else if (source.is(DamageTypeTags.IS_EXPLOSION)) return "EXPLOSIVE";
        else if (source.is(DamageTypeTags.IS_FIRE)) return "FIRE";
        else if (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) ||
                source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)) return "MELEE";
        return "NONE";
    }

    public boolean hasResistance(DamageSource source) {
        return switch (damageType) {
            case "PROJECTILE" -> source.is(DamageTypeTags.IS_PROJECTILE);
            case "EXPLOSIVE" -> source.is(DamageTypeTags.IS_EXPLOSION);
            case "FIRE" -> source.is(DamageTypeTags.IS_FIRE);
            case "DIRECT" -> (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO));
            default -> false;
        };
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.damageType, this.stacks);
    }
}