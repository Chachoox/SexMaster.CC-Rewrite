package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Clip extends Module {
    public Clip() {
        super("Clip", Category.MOVEMENT);
    }

    private final Value<Boolean> packet = new Value<>("Packet", true);
    private final Value<Boolean> mini = new Value<>("Mini", false);
    private final Value<Boolean> custom = new Value<>("CustomMotion", true);
    private final Value<Double> powers = new Value<>("Power", 0.1D,0.0D, 0.5D);

    @SubscribeEvent
    public void onUpdate(UpdateEvent event){
        if (isNull()) return;
        double yaw = mc.player.rotationYaw * 0.017453292;
        double power = powers.getValue();
        if (mc.gameSettings.keyBindSneak.isKeyDown()){

            if (custom.getValue()) {

                mc.player.setPositionAndUpdate(mc.player.posX - Math.sin(yaw) * power, mc.player.posY + 0.03f, mc.player.posZ + Math.cos(yaw) * power);

            }
            if (packet.getValue()){

                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX - Math.sin(yaw) * power + 0.03f, mc.player.posY - 0.003f, mc.player.posZ + Math.cos(yaw) * power + 0.02, false));

            }
            if (mini.getValue()){

                mc.player.setPositionAndUpdate(mc.player.posX - Math.sin(yaw) * 0.0010, mc.player.posY + 0.01f, mc.player.posZ + Math.cos(yaw) * 0.0010);

            }
        }
    }
}
