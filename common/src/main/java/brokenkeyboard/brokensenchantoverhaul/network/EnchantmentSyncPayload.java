package brokenkeyboard.brokensenchantoverhaul.network;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record EnchantmentSyncPayload(Map<ResourceKey<Enchantment>, Integer> enchantments) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EnchantmentSyncPayload> TYPE = new CustomPacketPayload.Type<>(ModRegistry.location("enchantment_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(ConcurrentHashMap::new, ResourceKey.streamCodec(Registries.ENCHANTMENT), ByteBufCodecs.INT), EnchantmentSyncPayload::enchantments,
            EnchantmentSyncPayload::new);

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
