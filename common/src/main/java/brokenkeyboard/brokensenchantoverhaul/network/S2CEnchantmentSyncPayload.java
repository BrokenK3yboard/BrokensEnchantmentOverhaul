package brokenkeyboard.brokensenchantoverhaul.network;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record S2CEnchantmentSyncPayload(Map<ResourceKey<Enchantment>, Integer> enchantments) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CEnchantmentSyncPayload> TYPE = new CustomPacketPayload.Type<>(ModRegistry.location("enchantment_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CEnchantmentSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(ConcurrentHashMap::new, ResourceKey.streamCodec(Registries.ENCHANTMENT), ByteBufCodecs.INT), S2CEnchantmentSyncPayload::enchantments,
            S2CEnchantmentSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
