package brokenkeyboard.brokensenchantoverhaul.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("UnusedReturnValue")
@Mixin(CrossbowItem.class)
public interface CrossbowItemAccessor {

    @Invoker
    static boolean callTryLoadProjectiles(LivingEntity shooter, ItemStack crossbowStack) {
        throw new UnsupportedOperationException();
    }
}
