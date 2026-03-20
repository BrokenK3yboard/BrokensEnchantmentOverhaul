package brokenkeyboard.brokensenchantoverhaul.mixin;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public interface ItemStackAccessor {

    @Invoker
    void callAddModifierTooltip(Consumer<Component> tooltipAdder, @Nullable Player player, Holder<Attribute> attribute, AttributeModifier modifier);
}
