package brokenkeyboard.brokensenchantoverhaul.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public record RepairEquippedItemEffect(LevelBasedValue repair) implements EnchantmentEntityEffect {

    public static final MapCodec<RepairEquippedItemEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("repair").forGetter(RepairEquippedItemEffect::repair)).apply(instance, RepairEquippedItemEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantLevel, EnchantedItemInUse item, Entity entity, Vec3 vec3) {
        if (!(entity instanceof LivingEntity living)) return;

        ArrayList<ItemStack> equippedItems = new ArrayList<>();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = living.getItemBySlot(slot);
            if (stack.isDamaged()) {
                equippedItems.add(stack);
            }
        }

        if (!equippedItems.isEmpty()) {
            ItemStack stack = equippedItems.get(level.random.nextInt(equippedItems.size()));
            stack.setDamageValue((int) (stack.getDamageValue() - repair.calculate(enchantLevel)));
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
