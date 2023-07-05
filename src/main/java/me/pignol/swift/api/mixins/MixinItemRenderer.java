package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.ViewmodelModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Final
    @Shadow
    private Minecraft mc;

    @Inject(method = "renderItemSide", at = @At("HEAD"))
    public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        boolean left = transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
        boolean right = transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
        if ((left || right) && ViewmodelModule.INSTANCE.isEnabled()) {
            GlStateManager.scale(ViewmodelModule.INSTANCE.scaleX.getValue(), ViewmodelModule.INSTANCE.scaleY.getValue(), ViewmodelModule.INSTANCE.scaleZ.getValue());
            /*GlStateManager.rotate(ViewmodelModule.INSTANCE.rotateX.getValue(), 1, 0, 0);
            GlStateManager.rotate(ViewmodelModule.INSTANCE.rotateY.getValue(), 0, 1, 0);
            GlStateManager.rotate(ViewmodelModule.INSTANCE.rotateZ.getValue(), 0, 0, 1);*/
        }
    }

    @Inject(method = "transformFirstPerson", at = @At("HEAD"))
    public void transformFirstPerson(EnumHandSide hand, float p_187453_2_, CallbackInfo ci) {
        if (ViewmodelModule.INSTANCE.isEnabled()) {
            boolean left = hand == EnumHandSide.LEFT;
            if (!ViewmodelModule.INSTANCE.pause.getValue() || checkHand(left)) {
                GlStateManager.translate(ViewmodelModule.INSTANCE.translateX.getValue() * (left ? 1 : -1), ViewmodelModule.INSTANCE.translateY.getValue(), ViewmodelModule.INSTANCE.translateZ.getValue());
            }
        }
    }

    private boolean checkHand(boolean left) {
        if (mc.player.isHandActive()) {
            return (mc.player.getActiveHand() != EnumHand.OFF_HAND || !left) && (mc.player.getActiveHand() != EnumHand.MAIN_HAND || left);
        }
        return true;
    }

}
