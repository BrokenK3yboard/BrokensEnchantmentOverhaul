package brokenkeyboard.brokensenchantoverhaul.predicate;

import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.world.item.ItemStack;

public record HasBreachUses() implements ItemSubPredicate {

    public static final MapCodec<HasBreachUses> CODEC = MapCodec.unit(new HasBreachUses());

    public static HasBreachUses hasBreachUses() {
        return new HasBreachUses();
    }

    @Override
    public boolean matches(ItemStack stack) {
        return stack.getOrDefault(ModRegistry.BREACH_USES, 0) > 0;
    }
}
