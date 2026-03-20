package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.CommonHandler;
import brokenkeyboard.brokensenchantoverhaul.enchantment.ConditionalAttributeEffect;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.client.event.GatherSkippedAttributeTooltipsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackNFMixin {

    @WrapOperation(method = "getTooltipLines", at = @At(value = "INVOKE",
            target = "Lnet/neoforged/neoforge/common/util/AttributeUtil;addAttributeTooltips(Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;Lnet/neoforged/neoforge/common/util/AttributeTooltipContext;)V"))
    private void modifyTooltip(ItemStack stack, Consumer<Component> tooltip, AttributeTooltipContext ctx, Operation<Void> original) {
        Level level = ctx.level();
        Player player = ctx.player();

        if (level != null && player != null) {
            ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            if (modifiers.showInTooltip()) {
                GatherSkippedAttributeTooltipsEvent event = NeoForge.EVENT_BUS.post(new GatherSkippedAttributeTooltipsEvent(stack, ctx));
                if (!event.isSkippingAll()) {
                    EquipmentSlotGroup[] slots = EquipmentSlotGroup.values();
                    for (EquipmentSlotGroup group : slots) {
                        if (!event.isSkipped(group)) {
                            Multimap<Holder<Attribute>, AttributeModifier> attributeMap = ConditionalAttributeEffect.collectAttributes(level, stack, player, group, modifiers);
                            attributeMap.values().removeIf((attributeModifiers) -> event.isSkipped(attributeModifiers.id()));
                            if (!attributeMap.isEmpty()) {
                                tooltip.accept(Component.empty());
                                tooltip.accept(Component.translatable("item.modifiers." + group.getSerializedName()).withStyle(ChatFormatting.GRAY));
                                AttributeUtil.applyTextFor(stack, tooltip, attributeMap, ctx);
                            }
                        }
                    }
                }
            }
            NeoForge.EVENT_BUS.post(new AddAttributeTooltipsEvent(stack, tooltip, ctx));
        } else {
            original.call(stack, tooltip, ctx);
        }
    }

    @WrapOperation(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processDurabilityChange(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I"))
    private int modifyDurabilityLoss(ServerLevel level, ItemStack stack, int damage, Operation<Integer> original) {
        int originalDamage = original.call(level, stack, damage);
        return CommonHandler.modifyDurabilityLoss(level.random, stack, originalDamage);
    }
}
