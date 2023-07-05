package me.pignol.swift.api.mixins;

import me.pignol.swift.client.event.events.Render2DEvent;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.modules.render.BlockHighlightModule;
import me.pignol.swift.client.modules.render.NametagsModule;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"))
    public void renderGameOverlay(float partialTicks, long nanoTime, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new Render2DEvent());
    }
    
    @Inject(method = "renderHand", at = @At("HEAD"))
    public void renderHand(float partialTicks, int pass, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new Render3DEvent());
    }

    @Inject(method = "isDrawBlockOutline", at = @At("HEAD"), cancellable = true)
    public void isDrawblockoutline(CallbackInfoReturnable<Boolean> cir) {
        if (BlockHighlightModule.INSTANCE.isEnabled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "drawNameplate", at = @At("HEAD"), cancellable = true)
    private static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, CallbackInfo ci) {
        if (NametagsModule.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }

}
