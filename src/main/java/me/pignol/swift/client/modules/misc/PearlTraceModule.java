package me.pignol.swift.client.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.mixins.AccessorRenderManager;
import me.pignol.swift.api.util.ChatUtil;
import me.pignol.swift.api.util.DirectionUtil;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class PearlTraceModule extends Module {

    private final Value<Boolean> notifyInChat = new Value<>("Notify", true);
    private final Value<Boolean> render = new Value<>("Render", true);
    private final Value<Double> time = new Value<>("Time", 20.0, 5.0D, 0.0, v -> render.getValue());
    private final Value<Float> lineWidth = new Value<>("LineWidth", 1.0F, 0.1F, 5.0F, v -> render.getValue());

    private final HashMap<UUID, List<Vec3d>> poses = new HashMap<>();
    private final HashMap<UUID, Double> timeMap = new HashMap<>();

    public PearlTraceModule() {
        super("PearlTrace", Category.MISC);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        for (Map.Entry<UUID, Double> e : new HashMap<>(this.timeMap).entrySet()) {
            UUID uuid = e.getKey();
            double time = e.getValue();
            if (time <= 0.0D) {
                poses.remove(uuid);
                this.timeMap.remove(uuid);
            } else {
                this.timeMap.replace(uuid, time - 0.05D);
            }
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderPearl) {
                UUID uuid = entity.getUniqueID();
                if (!poses.containsKey(uuid)) {
                    EntityPlayer player = mc.world.getClosestPlayer(entity.posX, entity.posY, entity.posZ, 3.0F, ent -> ent != mc.player);
                    if (player != null) {
                        String playerName = player.getName();
                        playerName = (FriendManager.getInstance().isFriend(playerName) ? ChatFormatting.AQUA : ChatFormatting.LIGHT_PURPLE) + playerName + ChatFormatting.LIGHT_PURPLE;
                        ChatUtil.printString(playerName + " threw a pearl going " + DirectionUtil.convertToCoords(entity.getHorizontalFacing()));
                        break;
                    }
                    List<Vec3d> list = new ArrayList<>();
                    list.add(entity.getPositionVector());
                    this.poses.put(uuid, list);
                    this.timeMap.put(uuid, time.getValue());
                } else {
                    this.timeMap.replace(uuid, time.getValue());
                    List<Vec3d> v = this.poses.get(uuid);
                    v.add(entity.getPositionVector());
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (render.getValue()) {
            RenderUtil.enableGL3D();
            GL11.glLineWidth(lineWidth.getValue());
            double renderPosX = ((AccessorRenderManager) mc.getRenderManager()).getRenderPosX();
            double renderPosY = ((AccessorRenderManager) mc.getRenderManager()).getRenderPosY();
            double renderPosZ = ((AccessorRenderManager) mc.getRenderManager()).getRenderPosZ();
            for (List<Vec3d> list : poses.values()) {
                GL11.glBegin(1);

                GL11.glColor3d(1, 1, 1);
                for (int i = 1; i < list.size(); ++i) {
                    GL11.glVertex3d((list.get(i)).x - renderPosX, list.get(i).y - renderPosY, list.get(i).z - renderPosZ);
                    GL11.glVertex3d((list.get(i - 1)).x - renderPosX,  list.get(i - 1).y - renderPosY, list.get(i - 1).z - renderPosZ);
                }

                GL11.glEnd();
            }
            RenderUtil.disableGL3D();
        }
    }

}
