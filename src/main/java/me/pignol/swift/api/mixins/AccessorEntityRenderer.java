package me.pignol.swift.api.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface AccessorEntityRenderer {

    @Invoker
    void invokeSetupCameraTransform(float partialTicks, int pass);

    @Invoker
    void invokeOrientCamera(float partialTicks);

    @Accessor("prevFrameTime")
    long getPrevFrameTime();

}
