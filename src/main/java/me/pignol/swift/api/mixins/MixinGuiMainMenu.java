package me.pignol.swift.api.mixins;

import me.pignol.swift.api.util.render.RenderUtil;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {

    private static final ResourceLocation LOLI = new ResourceLocation("pig.png");

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        //FontManager.getInstance().getCFontRenderer().drawString("TrollGod.CC", 2, 2, -1, true);
        //FontManager.getInstance().getCFontRenderer().drawString("Logged in as " + mc.getSession().getUsername(), 2, 12, -1, true);
    }

    /**
     * @author Yo,
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //ScaledResolution resolution = new ScaledResolution(mc);
        //mc.getTextureManager().bindTexture(LOLI);
        //Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), resolution.getScaledWidth(), resolution.getScaledHeight());
        RenderUtil.drawRect(-1, -1, 1920, 1080, 0XFF555555);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }


}
