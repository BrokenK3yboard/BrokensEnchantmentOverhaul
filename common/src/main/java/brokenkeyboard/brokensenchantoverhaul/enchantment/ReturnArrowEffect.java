package brokenkeyboard.brokensenchantoverhaul.enchantment;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record ReturnArrowEffect() implements EnchantmentEntityEffect {

    public static final MapCodec<ReturnArrowEffect> CODEC = MapCodec.unit(ReturnArrowEffect::new);

    @Override
    public void apply(ServerLevel serverLevel, int enchantLevel, EnchantedItemInUse itemInUse, Entity entity, Vec3 vec3) {
        if (itemInUse.owner() instanceof Player player && !player.getInventory().add(new ItemStack(Items.ARROW))) {
            player.drop(new ItemStack(Items.ARROW), false);
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
