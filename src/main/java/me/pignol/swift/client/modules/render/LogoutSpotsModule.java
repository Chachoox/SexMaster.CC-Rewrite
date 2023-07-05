package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.mixins.AccessorRenderManager;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.ConnectionEvent;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.FontManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogoutSpotsModule extends Module {

    private final Value<Boolean> ghost = new Value<>("Ghost", true);

    private final Value<Float> size = new Value<>("Size", 6.4f, 0.1f, 20.0f);
    private final Value<Boolean> scaleing = new Value<>("Scale", false);
    private final Value<Float> factor = new Value<>("Factor", 0.5f, 0.1f, 1.0f);
    private final Value<Boolean> smartScale = new Value<>("SmartScale", false);
    private final Value<Float> range = new Value<>("Range", 300.0f, 50.0f, 500.0f);
    private final Value<Boolean> rect = new Value<>("Rectangle", true);
   // private final Value<Boolean>

    private final List<LogoutPos> spots = new CopyOnWriteArrayList<>();

    public LogoutSpotsModule() {
        super("LogoutSpots", Category.RENDER);
    }

    @Override
    public void onDisable() {
        spots.clear();
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (isNull())return;
       // spots.add(new LogoutPos(mc.player.getName(), mc.player.getUniqueID(), mc.player));
        if (!spots.isEmpty()) {
            synchronized (spots) {
                for (LogoutPos spot : spots) {
                    if (spot.getEntity() != null) {
                        EntityPlayer player = spot.getEntity();
                        if (ghost.getValue()) {
                            EntityPlayer ghost = spot.getEntity();
                            ghost.prevLimbSwingAmount = 0;
                            ghost.limbSwing = 0;
                            ghost.limbSwingAmount = 0;
                            ghost.hurtTime = 0;
                            ghost.resetActiveHand();

                            GlStateManager.pushMatrix();
                            GlStateManager.enableLighting();
                            GlStateManager.enableBlend();
                            GlStateManager.enableDepth();
                            GlStateManager.color(1, 1, 1, 0.4F);
                            mc.getRenderManager().renderEntity(ghost, spot.getX() - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosX(), spot.getY() - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosY(), spot.z - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosZ(), ghost.rotationYaw, mc.getRenderPartialTicks(), false);
                            GlStateManager.disableLighting();
                            GlStateManager.disableBlend();
                            GlStateManager.popMatrix();
                        }

                        player.setPosition(
                                interpolate(player.lastTickPosX, player.posX, mc.getRenderPartialTicks()),
                                interpolate(player.lastTickPosY, player.posY, mc.getRenderPartialTicks()),
                                interpolate(player.lastTickPosZ, player.posZ, mc.getRenderPartialTicks())
                        );

                        player.prevRotationPitch = player.rotationPitch;
                        player.prevRotationYaw = player.rotationYaw;
                        player.prevRotationYawHead = player.rotationYawHead;

                        double x = player.posX - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosX();
                        double y = player.posY - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosY();
                        double z = player.posZ - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosZ();
                        renderNameTag(spot.getName(), x, y, z, mc.getRenderPartialTicks(), spot.getX(), spot.getY(), spot.getZ());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onDisconect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        spots.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUpdate(UpdateEvent event) {
        if (!isNull()) {
            spots.removeIf(spot -> mc.player.getDistanceSq(spot.getEntity()) >= (range.getValue() * range.getValue()));
        }
    }

    @SubscribeEvent
    public void onConnection(ConnectionEvent event) {
        if(event.getStage() == 0) {
            UUID uuid = event.getUuid();
            EntityPlayer entity = mc.world.getPlayerEntityByUUID(uuid);
            /*if (entity != null) {
                if(message.getValue()) {
                    Command.sendMessage(TextUtil.GREEN + entity.getName() + " just logged in"+ (coords.getValue() ? " at (" + (int) entity.posX + ", " + (int) entity.posY + ", " + (int) entity.posZ + ")!" : "!"),notification.getValue());
                }
            }*/
            spots.removeIf(pos -> pos.getName().equalsIgnoreCase(event.getName()));
        } else if(event.getStage() == 1) {
            EntityPlayer entity = event.getEntity();
            UUID uuid = event.getUuid();
            String name = event.getName();
            /*if(message.getValue()) {
                Command.sendMessage(TextUtil.RED + event.getName() + " just logged out"+ (coords.getValue() ? " at (" + (int) entity.posX + ", " + (int) entity.posY + ", " + (int) entity.posZ + ")!" : "!"),notification.getValue());
            }*/
            if(name != null && entity != null && uuid != null) {
                spots.add(new LogoutPos(name, uuid, entity));
            }
        }
    }

    private void renderNameTag(String name, double x, double yi, double z, float delta, double xPos, double yPos, double zPos) {
        double y = yi + 0.7D;
        Entity camera = mc.getRenderViewEntity();
        assert camera != null;
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);

        String displayTag = name + " logged out at " + String.format("%.2f", xPos) + ", " + String.format("%.2f", yPos) + ", " + String.format("%.2f", zPos);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = FontManager.getInstance().getStringWidth(displayTag) / 2;
        double scale = (0.0018 + size.getValue() * (distance * factor.getValue())) / 1000.0;

        if (distance <= 8 && smartScale.getValue()) {
            scale = 0.0245D;
        }

        if (!scaleing.getValue()) {
            scale = size.getValue() / 100.0;
        }

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1, -1500000);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) y + 1.4F, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();

        if (rect.getValue()) {
            RenderUtil.drawRect(-width - 2, -(FontManager.getInstance().getHeight()), width + 2F, 2F, 0x55000000);
        }

        GlStateManager.disableDepth();
        FontManager.getInstance().drawStringWithShadow(displayTag, -width, -(FontManager.getInstance().getHeight() - 1), ColorsModule.INSTANCE.getColor().getRGB());

        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1, 1500000);
        GlStateManager.popMatrix();
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }

    private static class LogoutPos {

        private final String name;
        private final UUID uuid;
        private final EntityPlayer entity;
        private final double x;
        private final double y;
        private final double z;

        public LogoutPos(String name, UUID uuid, EntityPlayer entity) {
            this.name = name;
            this.uuid = uuid;
            this.entity = entity;
            this.x = entity.posX;
            this.y = entity.posY;
            this.z = entity.posZ;
        }


        public String getName() {
            return name;
        }

        public UUID getUuid() {
            return uuid;
        }

        public EntityPlayer getEntity() {
            return entity;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }

}
