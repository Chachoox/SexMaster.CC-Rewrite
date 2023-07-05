package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.EnchantGlintModule;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {


    @Shadow
    public abstract void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack);

    @ModifyArg(method = "renderEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index = 1)
    public int renderEffect(int color) {
        int clr = ((0xFF) << 24) |
                ((EnchantGlintModule.INSTANCE.red.getValue() & 0xFF) << 16) |
                ((EnchantGlintModule.INSTANCE.green.getValue() & 0xFF) << 8)  |
                ((EnchantGlintModule.INSTANCE.blue.getValue() & 0xFF));
        return (EnchantGlintModule.INSTANCE.isEnabled() ? clr : color);
    }

    /**
     * @author e
     * @reason removed some forge shit for fsater rendering
     */
    @Overwrite
    private void renderModel(IBakedModel model, int color, ItemStack stack) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        for (EnumFacing enumfacing : EnumFacing.values()) {
            this.renderQuads(bufferbuilder, model.getQuads(null, enumfacing, 0L), color, stack);
        }

        this.renderQuads(bufferbuilder, model.getQuads(null, null, 0L), color, stack);
        tessellator.draw();
    }



}
