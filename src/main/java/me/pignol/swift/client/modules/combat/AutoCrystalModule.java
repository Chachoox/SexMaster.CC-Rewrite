package me.pignol.swift.client.modules.combat;

import me.pignol.swift.api.mixins.AccessorCPacketUseEntity;
import me.pignol.swift.api.util.MathUtil;
import me.pignol.swift.api.util.TimerUtil;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AutoCrystalModule extends Module {

    public static final AutoCrystalModule INSTANCE = new AutoCrystalModule();

    private final Value<Settings> settings = new Value<>("Setting", Settings.PLACE);

    //PLACE
    private final Value<Boolean> place = new Value<>("Place", true, v -> settings.getValue() == Settings.PLACE);
    public final Value<Float> placeRange = new Value<>("PlaceRange", 6.0f, 0.0f, 10.0f, v -> settings.getValue() == Settings.PLACE);
    private final Value<Integer> placeDelay = new Value<>("PlaceDelay", 0, 0, 1000, v -> settings.getValue() == Settings.PLACE && place.getValue());
    private final Value<Float> placeTrace = new Value<>("PlaceTrace", 4.0f, 0.0f, 6.0f, v -> settings.getValue() == Settings.PLACE && place.getValue());
    private final Value<Boolean> second = new Value<>("Second", true, v -> settings.getValue() == Settings.PLACE && place.getValue());
    private final Value<Boolean> properTrace = new Value<>("ProperTrace", true, v -> settings.getValue() == Settings.PLACE && place.getValue());

    //DAMAGE
    private final Value<Float> minDamage = new Value<>("MinDamage", 4.2F, 0.0F, 36.0F, v -> settings.getValue() == Settings.DAMAGE);
    private final Value<Float> facePlaceHealth = new Value<>("Faceplace", 4.2F, 0.0F, 36.0F, v -> settings.getValue() == Settings.DAMAGE);
    private final Value<Float> maxSelf = new Value<>("MaxSelf", 4.2F, 0.0F, 36.0F, v -> settings.getValue() == Settings.DAMAGE);
    private final Value<Integer> armorScale = new Value<>("Armor", 10, 0, 100, v -> settings.getValue() == Settings.DAMAGE);

    //BREAK
    private final Value<Boolean> explode = new Value<>("Break", true, v -> settings.getValue() == Settings.BREAK);
    private final Value<Integer> breakDelay = new Value<>("BreakDelay", 0, 0, 400, v -> settings.getValue() == Settings.BREAK && explode.getValue());
    private final Value<Float> breakRange = new Value<>("BreakRange", 6.0f, 0.0f, 6.0f, v -> settings.getValue() == Settings.BREAK && explode.getValue());
    private final Value<Float> breakTrace = new Value<>("BreakTrace", 3.5f, 0.0f, 6.0f, v -> settings.getValue() == Settings.BREAK && explode.getValue());
    private final Value<Boolean> predict = new Value<>("Predict", true, v -> settings.getValue() == Settings.BREAK && explode.getValue());

    //RENDER
    private final Value<Boolean> slop = new Value<>("Sync", true, v -> this.settings.getValue() == Settings.RENDER);
    private final Value<Integer> red = new Value<>("Red", 255, 0, 255, v -> this.settings.getValue() == Settings.RENDER && !slop.getValue());
    private final Value<Integer> green = new Value<>("Green", 255, 0, 255, v -> this.settings.getValue() == Settings.RENDER && !slop.getValue());
    private final Value<Integer> blue = new Value<>("Blue", 255, 0, 255, v -> this.settings.getValue() == Settings.RENDER && !slop.getValue());
    private final Value<Integer> alpha = new Value<>("Alpha", 60, 0, 255, v -> this.settings.getValue() == Settings.RENDER);
    private final Value<Boolean> outline = new Value<>("Outline", true, v -> this.settings.getValue() == Settings.RENDER);
    private final Value<Float> lineWidth = new Value<>("LineWidth", 1.0F, 0.1F, 3.0F, v -> settings.getValue() == Settings.RENDER && outline.getValue());
    private final Value<Boolean> outlineSync = new Value<>("OutlineSync", true, v -> this.settings.getValue() == Settings.RENDER && outline.getValue());
    private final Value<Integer> outlineRed = new Value<>("OutRed", 255, 0, 255, v -> this.settings.getValue() == Settings.RENDER && outline.getValue() && !outlineSync.getValue());
    private final Value<Integer> outlineGreen = new Value<>("OutGreen", 255, 0, 255, v -> this.settings.getValue() == Settings.RENDER && outline.getValue() && !outlineSync.getValue());
    private final Value<Integer> outlineBlue = new Value<>("OutBlue", 255, 0, 255, v -> this.settings.getValue() == Settings.RENDER && outline.getValue() && !outlineSync.getValue());

    //MISC
    private final Value<Float> range = new Value<>("Range", 12.0f, 0.1f, 20.0f, v -> settings.getValue() == Settings.MISC);

    public static EntityPlayer target = null;

    private final List<BlockPos> placedPos = new ArrayList<>();

    private final TimerUtil breakTimer = new TimerUtil();
    private final TimerUtil placeTimer = new TimerUtil();
    private final TimerUtil renderTimer = new TimerUtil();


    private Entity efficientTarget;
    private BlockPos placePos;

    private BlockPos renderPos;
    private boolean mainHand;
    private boolean offHand;

    public AutoCrystalModule() {
        super("AutoCrystal", Category.COMBAT);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(TickEvent.ClientTickEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!place.getValue() && !explode.getValue())
            return;

        if (event.getPacket() instanceof SPacketSpawnObject && explode.getValue() && predict.getValue()) {
            SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                BlockPos pos = new BlockPos(packet.getX(), packet.getY() - 1, packet.getZ());
                if (placedPos.contains(pos) && mc.player.getDistanceSq(pos) < MathUtil.square(breakRange.getValue())) {
                    AccessorCPacketUseEntity accessor = (AccessorCPacketUseEntity) new CPacketUseEntity();
                    accessor.setEntityId(packet.getEntityID());
                    accessor.setAction(CPacketUseEntity.Action.ATTACK);
                    mc.getConnection().sendPacket((Packet<?>) accessor);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    breakTimer.reset();
                }
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36) {
                        entity.setDead();
                    }
                }
            }
        }

        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = (SPacketDestroyEntities) event.getPacket();
            for (int id : packet.getEntityIDs()) {
                Entity entity = mc.world.getEntityByID(id);
                if (entity instanceof EntityEnderCrystal) {
                    placedPos.remove(new BlockPos(entity.getPositionVector()).down());
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if ((offHand || mainHand) && renderPos != null) {
            GlStateManager.glLineWidth(lineWidth.getValue());
            Color color = slop.getValue() ? ColorsModule.INSTANCE.getColor() : new Color(red.getValue(), green.getValue(), blue.getValue());
            Color outlineColor = outlineSync.getValue() ? color : new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue());
            RenderUtil.drawBox(renderPos, color, outlineColor,outline.getValue(), alpha.getValue());
        }
    }

    public enum Settings {
        PLACE,
        BREAK,
        DAMAGE,
        RENDER,
        MISC
    }


}
