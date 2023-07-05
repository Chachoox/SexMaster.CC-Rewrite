package me.pignol.swift.client.modules.player;

import me.pignol.swift.api.util.RotationUtil;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.KeyPressEvent;
import me.pignol.swift.client.event.events.Render2DEvent;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

public class AntiAim extends Module {
    boolean right = false;
    boolean left = false;
    boolean up = true;
    boolean down = false;
    int aimPitch;
    int aimYaw;
    private Value<Boolean> arrows = new Value<>("Arrows", false);
    private List<Vector3d> locations;
    public AntiAim() {
        super("AntiAim", Category.PLAYER); }


        @SubscribeEvent
        public void onUpdate(UpdateEvent event) {
            if (event.getStage() == Stage.PRE) {
                if(right) {
                    float yawRight = mc.player.rotationYaw + 90;
                    RotationManager.getInstance().setYaw(-90);

                }
                if(left) {
                    float yawLeft = mc.player.rotationYaw + -90.0f;
                    RotationManager.getInstance().setYaw(yawLeft);
                }
                if(down) {
                    RotationManager.getInstance().setYaw(- 180.0f);
                }
                if(up) {
                    this.aimPitch = -180;
                    if (this.aimYaw + 60 > 360.0) {
                        this.aimYaw = 0;
                    }
                    this.aimYaw += (int)60;
                    RotationManager.getInstance().setYaw (aimYaw);
                }
            }
        }

        @SubscribeEvent
        public void onKeypress(KeyPressEvent event) {
            if(event.getKey() == Keyboard.KEY_RIGHT) {
                right = !right;
                left = false;
                down = false;
                up = false;
            }
            if(event.getKey() == Keyboard.KEY_LEFT) {
                left = !left;
                right = false;
                down = false;
                up = false;
            }
            if(event.getKey() == Keyboard.KEY_DOWN) {
                down = !down;
                left = false;
                right = false;
                up = false;
            }
            if(event.getKey() == Keyboard.KEY_UP) {
                up = !up;
                left = false;
                right = false;
                down = false;
            }

        }
        /*
        @SubscribeEvent
        public void onRender(Render2DEvent event) {
            if(arrows.getValue()) {
                if (!(Minecraft.getMinecraft().currentScreen instanceof GuiInventory)) {
                    RenderUtil.drawArrow(476.5F, 265, false, down ? 0xFF2e90db : -1);
                    RenderUtil.drawArrow(486, 252, true, right ? 0xFF2e90db : -1);
        GlStateManager.pushMatrix();
        GlStateManager.translate(486 + 90 - 10, 252 + 5, 0);
        GlStateManager.rotate((float) this.angel, 0, 0, -1);
        GlStateManager.translate(-(486 + 90 - 10), -(252 + 5), 0);
        RenderUtils.drawLeftArrow(647.5F, 268,  up ? 0xFF2e90db : -1);
        GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(486 + 90 - 10, 252 + 5, 0);
                    GlStateManager.rotate((float) this.angel2, 0, 0, -1);
                    GlStateManager.translate(-(486 + 90 - 10), -(252 + 5), 0);
                    RenderUtil.drawLeftArrow(559.5F, 350, left ? 0xFF2e90db : -1);
                    GlStateManager.popMatrix();
                }
            }
        }
        @SubscribeEvent
        public void onWorld(Render3DEvent event) {
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor3d(1, 1, 1);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (Vector3d vector : this.locations) {
                GL11.glVertex3d(vector.x - mc.getRenderManager().renderPosX,
                        vector.y - mc.getRenderManager().renderPosY,
                        vector.z - mc.getRenderManager().renderPosZ);
            }
            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();

            */
        }