package me.pignol.swift.client.modules.combat;

import me.pignol.swift.api.interfaces.mixin.INetworkManager;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CriticalsModule extends Module {

    private final Value<Boolean> boats = new Value<>("Boats", true);
    private final Value<Integer> boatHits = new Value<>("BoatHits", 5, 1, 10);

    public CriticalsModule() {
        super("Criticals", Category.COMBAT);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity && !isNull()) {
            final CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (mc.player.onGround && packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                final Entity entity = packet.getEntityFromWorld(mc.world);
                if (boats.getValue() && entity instanceof EntityBoat) {
                    for (int i = 0; i < boatHits.getValue(); ++i) {
                        ((INetworkManager) mc.getConnection().getNetworkManager()).sendPacketNoEvent(new CPacketUseEntity(entity));
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                    return;
                }
                if (entity instanceof EntityLivingBase) {
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1F, mc.player.posZ, false));
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                }
            }
        }
    }

}
