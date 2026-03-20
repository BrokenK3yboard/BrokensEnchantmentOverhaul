package brokenkeyboard.brokensenchantoverhaul.enchantment;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public record BlacksmithRepairEffect() implements OnKillEffect {

    public static final MapCodec<BlacksmithRepairEffect> CODEC = MapCodec.unit(BlacksmithRepairEffect::new);

    @Override
    public void apply(int enchantmentLevel, LivingEntity victim, DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity attacker && source.getWeaponItem() != null)) return;

        if (attacker.fallDistance >= 1.5F) {
            ArrayList<ItemStack> repairable = new ArrayList<>();

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = attacker.getItemBySlot(slot);
                if (stack.isDamageableItem() && stack.isDamaged()) {
                    repairable.add(stack);
                }
            }

            ItemStack toRepair = repairable.get(attacker.getRandom().nextInt(repairable.size()));
            toRepair.setDamageValue(toRepair.getDamageValue() - (int) Math.floor(attacker.fallDistance));
        }
    }

    @Override
    public MapCodec<? extends OnKillEffect> codec() {
        return CODEC;
    }
}
