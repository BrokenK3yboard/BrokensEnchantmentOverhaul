package brokenkeyboard.brokensenchantoverhaul.mixin;

import brokenkeyboard.brokensenchantoverhaul.render.RenderHelper;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
public abstract class RenderBuffersFabricMixin {

    @Shadow
    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> mapBuilders, RenderType renderType) {
        throw new AssertionError("Error shadowing RenderBuffers#put");
    }

    @Inject(method = "method_54639", at = @At(value = "TAIL"))
    private void addBuffer(Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> map, CallbackInfo ci) {
        put(map, RenderHelper.ALTERNATE_GLINT);
        put(map, RenderHelper.ALTERNATE_GLINT_TRANSLUCENT);
    }
}
