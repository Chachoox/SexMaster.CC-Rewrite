package me.pignol.swift.client.modules.misc;

import com.mojang.authlib.GameProfile;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.event.events.ValueEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class FakePlayerModule extends Module {

    private final Value<Boolean> record = new Value<>("Record", true);
    private final Value<Boolean> play = new Value<>("Play", true);

    private final Queue<Vec3d> recordedPositions = new LinkedList<>();

    private EntityOtherPlayerMP fake;

    public FakePlayerModule() {
        super("FakePlayer", Category.MISC);
    }

    @SubscribeEvent
    public void onValueChange(ValueEvent event) {
        if (event.getValue() == record && record.getValue()) {
            recordedPositions.clear();
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (record.getValue()) {
            recordedPositions.add(mc.player.getPositionVector());
        }

        if (fake == null)
            return;

        fake.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));

        if (play.getValue() && fake != null) {
            Vec3d vec = recordedPositions.poll();
            if (vec != null) {
                fake.setPosition(vec.x, vec.y, vec.z);
            } else {
                play.setValue(false);
            }
        }
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            setEnabled(false);
            return;
        }

        if (fake == null) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "fakeplayer");
            fake = new EntityOtherPlayerMP(mc.world, profile);
            fake.inventory.copyInventory(mc.player.inventory);
            fake.copyLocationAndAnglesFrom(mc.player);
            fake.setHealth(mc.player.getHealth());
            mc.world.addEntityToWorld(-999, fake);
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (fake != null) {
            mc.world.removeEntity(fake);
            fake = null;
        }
    }

}
