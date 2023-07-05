package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.mixins.AccessorEntityRenderer;
import me.pignol.swift.api.mixins.AccessorRenderManager;
import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.api.util.MathUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TracersModule extends Module {

    public Value<Boolean> invisibles = new Value<>("Invisibles", false);
    public Value<Float> width = new Value<>("Width", 1.0f, 0.1f, 5.0f);
    public Value<Integer> distance = new Value<>("Radius", 300, 0, 300);
    public Value<Integer> alpha = new Value<>("Alpha", 255, 0, 255);

    public TracersModule() {
        super("Tracers", Category.RENDER);
    }


    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (isNull()) {
            return;
        }

        GlStateManager.pushMatrix();

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        for (EntityPlayer entity : mc.world.playerEntities) {
            if (entity == mc.player) {
                continue;
            }
            double distance = mc.player.getDistanceSq(entity);
            if (distance < MathUtil.square(this.distance.getValue()) && !EntityUtil.isDead(entity)) {
                if (invisibles.getValue() || !entity.isInvisible()) {
                    drawLineToEntity(entity, getColorByDistance(entity, distance));
                }
            }
        }

        GlStateManager.popMatrix();
    }

    public double interpolate(double now, double then) {
        return then + (now - then) * mc.getRenderPartialTicks();
    }

    public void drawLineToEntity(Entity entity, Color color) {
        double posX = interpolate(entity.posX, entity.lastTickPosX) - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosX();
        double posY = interpolate(entity.posY, entity.lastTickPosY) - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosY();
        double posZ = interpolate(entity.posZ, entity.lastTickPosZ) - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosZ();
        drawLine(posX, posY, posZ, entity.height, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, alpha.getValue() / 255.0F);
    }

    public void drawLine(double posx, double posy, double posz, double up, float red, float green, float blue, float opacity) {
        Vec3d eyes = new Vec3d(0, 0, 1)
                .rotatePitch(-(float) Math
                        .toRadians(mc.player.rotationPitch))
                .rotateYaw(-(float) Math
                        .toRadians(mc.player.rotationYaw));

        drawLineFromPosToPos(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, posx, posy, posz, up, red, green, blue, opacity);
    }

    public void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(width.getValue());
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, opacity);
        GlStateManager.disableLighting();
        GL11.glLoadIdentity();
        ((AccessorEntityRenderer) mc.entityRenderer).invokeOrientCamera(mc.getRenderPartialTicks());
        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(posx, posy, posz);
            GL11.glVertex3d(posx2, posy2, posz2);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor3d(1d, 1d, 1d);
        GlStateManager.enableLighting();
    }


    public Color getColorByDistance(Entity entity, double distance) {
        if (entity instanceof EntityPlayer && FriendManager.getInstance().isFriend(entity.getName())) {
            return ColorsModule.INSTANCE.getFriendColor();
        }
        return new Color(Color.HSBtoRGB((float) (Math.max(0.0F, Math.min(distance, 2000) / 2000) / 3.0F), 1.0F, 1.0f) | 0xFF000000);
    }

}
