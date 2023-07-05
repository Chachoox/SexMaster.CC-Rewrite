package me.pignol.swift.api.mixins;

import me.pignol.swift.Swift;
import me.pignol.swift.client.event.events.KeyPressEvent;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "runTickKeyboard", at = @At(value = "INVOKE_ASSIGN", target = "org/lwjgl/input/Keyboard.getEventKeyState()Z", remap = false))
    private void runTickKeyboardHook(CallbackInfo callbackInfo) {
        MinecraftForge.EVENT_BUS.post(new KeyPressEvent(Keyboard.getEventKey(), Keyboard.getEventKeyState()));
    }

    @Inject(method = "shutdownMinecraftApplet", at = @At("HEAD"))
    public void shutdownMinecraft(CallbackInfo ci) {
        Swift.getInstance().unload();
    }

    @Inject(method = "getLimitFramerate", at = @At("HEAD"), cancellable = true)
    public void getLimitFramerate(CallbackInfoReturnable<Integer> cir) {
        if (!Display.isActive()) {
            cir.setReturnValue(ManageModule.INSTANCE.tabbedFps.getValue());
        }
    }
}
