package me.pignol.swift.client.managers;

import me.pignol.swift.api.util.Pair;
import me.pignol.swift.api.util.TimerUtil;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class HoleManager {

    private static final HoleManager INSTANCE = new HoleManager();

    public static HoleManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private final List<Pair<BlockPos, Boolean>> holes = new ArrayList<>();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final TimerUtil timer = new TimerUtil();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (event.getStage() == Stage.POST && timer.hasReached(ManageModule.INSTANCE.holeSearchDelay.getValue())) {
            holes.clear();
            //BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            final float radius = ManageModule.INSTANCE.holeRange.getValue();
            final float yRadius = ManageModule.INSTANCE.holeRangeY.getValue();
            final int posX = (int) mc.player.posX;
            final int posY = (int) mc.player.posY;
            final int posZ = (int) mc.player.posZ;
            final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int x = posX - (int) radius; x <= posX + radius; ++x) {
                for (int y = posY - (int) yRadius; y < posY + yRadius; ++y) {
                    for (int z = posZ - (int) radius; z <= posZ + radius; ++z) {
                        if ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y) < radius * radius) {
                            pos.setPos(x, y, z);

                            int originX = pos.getX();
                            int originY = pos.getY();
                            int originZ = pos.getZ();
                            int value = getHoleSafety(pos);
                            switch (value) {
                                case 2:
                                    holes.add(new Pair<>(new BlockPos(originX, originY, originZ), true));
                                    break;
                                case 1:
                                    holes.add(new Pair<>(new BlockPos(originX, originY, originZ), false));
                                    break;
                            }
                        }
                    }
                }
            }
            timer.reset();
        }
    }

    /**
     * @return bedrock hole returns 2, obsidian hole returns 1, no hole returns 0
     */
    public int getHoleSafety(BlockPos.MutableBlockPos offset) {
        int originX = offset.getX();
        int originY = offset.getY();
        int originZ = offset.getZ();
        for (int i = 0; i < 3; i++) {
            offset.setPos(originX, originY + i, originZ);
            if (mc.world.getBlockState(offset).getMaterial() != Material.AIR) {
                return 0;
            }
        }

        boolean bedrock = true;
        for (EnumFacing f : EnumFacing.values()) {
            if (f != EnumFacing.UP) {
                offset.setPos(originX + f.getXOffset(), originY + f.getYOffset(), originZ + f.getZOffset());
                Block block = mc.world.getBlockState(offset).getBlock();
                if (block != Blocks.BEDROCK) {
                    if (block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST) {
                        return 0;
                    }
                    bedrock = false;
                }
            }
        }
        offset.setPos(originX, originY, originZ);
        return bedrock ? 2 : 1;
    }

    public List<Pair<BlockPos, Boolean>> getHoles() {
        return holes;
    }

}
