package brokenkeyboard.brokensenchantoverhaul.render;

import brokenkeyboard.brokensenchantoverhaul.Constants;
import brokenkeyboard.brokensenchantoverhaul.ModRegistry;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class RenderHelper extends RenderType {

    public static final ResourceLocation ALTERNATE_GLINT_TEXTURE = ModRegistry.location("textures/misc/alternate_glint.png");

    public static final RenderType ALTERNATE_GLINT_TRANSLUCENT =
            create(Constants.MOD_ID + "_alternate_glint_translucent", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536,
                    RenderType.CompositeState.builder()
                            .setShaderState(RENDERTYPE_GLINT_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(ALTERNATE_GLINT_TEXTURE, true, false))
                            .setWriteMaskState(COLOR_WRITE)
                            .setCullState(NO_CULL)
                            .setDepthTestState(EQUAL_DEPTH_TEST)
                            .setTransparencyState(GLINT_TRANSPARENCY)
                            .setTexturingState(GLINT_TEXTURING)
                            .createCompositeState(false));

    public static final RenderType ALTERNATE_GLINT =
            create(Constants.MOD_ID + "_alternate_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536,
                    RenderType.CompositeState.builder()
                            .setShaderState(RENDERTYPE_GLINT_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(ALTERNATE_GLINT_TEXTURE, true, false))
                            .setWriteMaskState(COLOR_WRITE)
                            .setCullState(NO_CULL)
                            .setDepthTestState(EQUAL_DEPTH_TEST)
                            .setTransparencyState(GLINT_TRANSPARENCY)
                            .setTexturingState(GLINT_TEXTURING)
                            .createCompositeState(false));

    public RenderHelper(String name, VertexFormat format, VertexFormat.Mode mode, int bufSize, boolean hasCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufSize, hasCrumbling, sortOnUpload, setupState, clearState);
    }
}
