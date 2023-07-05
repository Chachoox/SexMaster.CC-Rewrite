package me.pignol.swift.api.mixins;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderItem.class)
public interface AccessorRenderItem {

    @Invoker
    void invokeRenderModel(IBakedModel model, int color);

}
