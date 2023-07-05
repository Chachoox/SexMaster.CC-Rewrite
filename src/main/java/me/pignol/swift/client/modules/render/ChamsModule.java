package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChamsModule extends Module {

    public static final ChamsModule INSTANCE = new ChamsModule();

    public final Value<Boolean> glint = new Value<>("Chams", true);
    public final Value<Integer> red = new Value<>("Red",0,0,255);
    public final Value<Integer> green = new Value<>("Green",0,0,255);
    public final Value<Integer> blue = new Value<>("Blue",0,0,255);
    public final Value<Integer> alpha = new Value<>("Alpha",0,0,255);
    public final Value<Boolean> wireframe = new Value<>("Wireframe", true);
    public final Value<Boolean> pack = new Value<>("Slop-On-My-Knob", true);

    //private Map<Entity, EntityInfo> positionHashMap = new HashMap<>();


    public ChamsModule() {
        super("Chams", Category.RENDER);
    }

     /* @SubscribeEvent
    public void onPacketRecieve(PacketEvent.Receive event) {
        if (interp.getValue()) {
            if (event.getPacket() instanceof SPacketEntity.S15PacketEntityRelMove  && ! isNull()) {
                SPacketEntity.S15PacketEntityRelMove packet = (SPacketEntity.S15PacketEntityRelMove) event.getPacket();
                Entity entity = (packet.getEntity(mc.world));
                if (entity == null)
                    return;
                positionHashMap.put(entity, new EntityInfo(new Vec3d(packet.getX(), packet.getY(), packet.getZ()), packet.getYaw(), packet.getPitch()));
            }
        }
    }

    @SubscribeEvent
    public void onRender3d(Render3DEvent event) {
        if(isNull())
            return;
        for (Entity entity : mc.world.playerEntities) {
            if(positionHashMap.containsKey(entity)) {
                EntityInfo info = positionHashMap.get(entity);
                entity.setPositionAndRotation(info.getPosition().x, info.getPosition().y, info.getPosition().z, info.getYaw(), info.getPitch());
            }
        }
    }

    public class EntityInfo {
        private Vec3d position;
        private float yaw, pitch;

        public EntityInfo(Vec3d position, float yaw, float pitch) {
            this.position = position;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public float getPitch() {
            return pitch;
        }

        public float getYaw() {
            return yaw;
        }

        public Vec3d getPosition() {
            return position;
        }
    } */
}
