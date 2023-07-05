package me.pignol.swift.api.mixins;

import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.other.ColorsModule;
import me.pignol.swift.client.modules.render.ChamsModule;
import me.pignol.swift.client.modules.render.EspModule;
import me.pignol.swift.client.modules.render.SkeletonModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(RenderLivingBase.class)
public class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {

    private static final ResourceLocation UNDERWATER = new ResourceLocation("textures/misc/underwater.png");
    private static final ResourceLocation PACK_PNG = new ResourceLocation("textures/misc/unknown_pack.png");

    @Shadow
    protected ModelBase mainModel;

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    public void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        if (SkeletonModule.INSTANCE.isEnabled()) {
            SkeletonModule.INSTANCE.onRenderModel(entitylivingbaseIn, mainModel);
        }
    }

    @Inject(method = "renderLayers", at = @At("RETURN"), cancellable = true)
    public void wireframeHook(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo ci) {
        if (ChamsModule.INSTANCE.wireframe.getValue()) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (FriendManager.getInstance().isFriend(entitylivingbaseIn.getName())) {
                GL11.glColor3f(ColorsModule.INSTANCE.getFriendColor().getRed() / 255.0F, ColorsModule.INSTANCE.getFriendColor().getGreen() / 255.0F, ColorsModule.INSTANCE.getFriendColor().getBlue() / 255.0F);
            } else {
                GL11.glColor3f(ColorsModule.INSTANCE.getColor().getRed() / 255.0F, ColorsModule.INSTANCE.getColor().getGreen() / 255.0F, ColorsModule.INSTANCE.getColor().getBlue() / 255.0F);
            }
            GL11.glLineWidth(EspModule.INSTANCE.lineWidth.getValue() / 10.0F);
            mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

            if (ChamsModule.INSTANCE.glint.getValue()) {
                renderEnchantedGlint(entitylivingbaseIn, mainModel, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
            }

        if (ChamsModule.INSTANCE.pack.getValue()) {
            renderPackPng(entitylivingbaseIn, mainModel, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
        }
    }

    private static void renderEnchantedGlint(EntityLivingBase base, ModelBase model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
        float f = (float)base.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        Minecraft.getMinecraft().renderEngine.bindTexture(UNDERWATER);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.color(ChamsModule.INSTANCE.red.getValue(), ChamsModule.INSTANCE.green.getValue(), ChamsModule.INSTANCE.blue.getValue(), ChamsModule.INSTANCE.alpha.getValue());
        for (int i = 0; i < 2; ++i) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
            GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.disableDepth();
            model.render(base, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
            GlStateManager.enableDepth();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
    }

    private static void renderPackPng(EntityLivingBase base, ModelBase model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
        float f = (float)base.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        Minecraft.getMinecraft().renderEngine.bindTexture(PACK_PNG);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.color(ChamsModule.INSTANCE.red.getValue(), ChamsModule.INSTANCE.green.getValue(), ChamsModule.INSTANCE.blue.getValue(), ChamsModule.INSTANCE.alpha.getValue());
        for (int i = 0; i < 2; ++i) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
            GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.disableDepth();
            model.render(base, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
            GlStateManager.enableDepth();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }

}
