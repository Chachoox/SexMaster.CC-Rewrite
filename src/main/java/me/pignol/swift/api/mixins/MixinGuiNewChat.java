package me.pignol.swift.api.mixins;


import me.pignol.swift.client.managers.FontManager;
import me.pignol.swift.client.modules.other.FontModule;
import me.pignol.swift.client.modules.render.NoRenderModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat extends Gui {

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    public void drawChat(int left, int top, int right, int bottom, int color) {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.chatBox.getValue()) {
            return;
        }
        drawRect(left, top, right, bottom, color);
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    public int drawStringHook(FontRenderer fontRenderer, String text, float x, float y, int color) {
        if (FontModule.INSTANCE.isEnabled() && FontModule.INSTANCE.syncChat.getValue()) {
            FontManager.getInstance().drawStringWithShadow(text, x, y, color);
            return 0;
        }
        return Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, color);
    }

}
