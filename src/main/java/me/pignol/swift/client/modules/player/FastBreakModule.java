package me.pignol.swift.client.modules.player;

import me.pignol.swift.api.mixins.AccessorPlayerControllerMP;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.TimerUtil;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.DamageBlockEvent;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class FastBreakModule extends Module {

    private final Value<Boolean> noDelay = new Value<>("NoDelay", true);
    private final Value<Boolean> reset = new Value<>("Reset", true);
    private final Value<Boolean> instant = new Value<>("Instant", false);
    private final Value<Integer> delay = new Value<>("Delay", 50, 0, 2000, v -> instant.getValue());
    private final Value<Integer> packetsPerBlock = new Value<>("Packets/Block", 5, 1, 100, v -> instant.getValue());
    private final Value<SwitchType> switchType = new Value<>("Switch", SwitchType.NONE);

    private final TimerUtil breakTimer = new TimerUtil();

    private IBlockState lastState;
    private DamageBlockEvent lastEvent;
    private int hitsOnCurrent;

    public FastBreakModule() {
        super("FastBreak", Category.PLAYER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (lastEvent != null && lastState != null) {
            if (lastState.getBlock() == Blocks.AIR || instant.getValue()) {
                RenderUtil.drawBoundingBox(lastEvent.getPos(), new Color(0x9900FCFF, true), true, 60);
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
        }

        if (reset.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && Mouse.isButtonDown(1)) {
            lastEvent = null;
            lastState = null;
        }

        if (noDelay.getValue()) {
            ((AccessorPlayerControllerMP) mc.playerController).setBlockHitDelay(0);
        }

        if (lastEvent != null) {
            lastState = mc.world.getBlockState(lastEvent.getPos());
            if (instant.getValue()) {
                if (lastState.getBlock() == Blocks.AIR) {
                    hitsOnCurrent = 0;
                    return;
                }
                if (breakTimer.hasReached(delay.getValue())) {
                    if (hitsOnCurrent > packetsPerBlock.getValue()) {
                        lastEvent = null;
                        hitsOnCurrent = 0;
                        return;
                    }
                    hitsOnCurrent++;
                    mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastEvent.getPos(), lastEvent.getFacing()));
                    breakTimer.reset();
                }
            }
        } else {
            hitsOnCurrent = 0;
        }
    }

    @SubscribeEvent
    public void onClickBlock(DamageBlockEvent event) {
        BlockPos pos = event.getPos();
        EnumFacing facing = event.getFacing();
        if (canBlockBeBroken(pos)) {
            event.setCanceled(true);
            int lastSlot = -1;
            if (switchType.getValue() != SwitchType.NONE && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe)) {
                int pickaxeSlot = ItemUtil.getSlotHotbar(ItemPickaxe.class);
                if (pickaxeSlot != -1) {
                    switch (switchType.getValue()) {
                        case SILENT:
                            lastSlot = mc.player.inventory.currentItem;
                            mc.getConnection().sendPacket(new CPacketHeldItemChange(pickaxeSlot));
                            break;
                        case NORMAL:
                            mc.player.inventory.currentItem = pickaxeSlot;
                            ItemUtil.syncHeldItem();
                            break;
                    }
                }
            }

            mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
            mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
            mc.player.swingArm(EnumHand.MAIN_HAND);

            if (lastSlot != -1) {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
            }

            lastState = mc.world.getBlockState(pos);
            lastEvent = event;
            breakTimer.reset();
        }
    }

    private boolean canBlockBeBroken(BlockPos pos) {
        final IBlockState state = mc.world.getBlockState(pos);
        final Block block = state.getBlock();
        return block.getBlockHardness(state, mc.world, pos) != -1;
    }

    public enum SwitchType {
        SILENT,
        NORMAL,
        NONE
    }

}
