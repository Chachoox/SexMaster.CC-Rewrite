package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockHighlightModule extends Module {

    public static BlockHighlightModule INSTANCE = new BlockHighlightModule();

    public BlockHighlightModule() {
        super("BlockHighlight", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            if (pos == null) {
                return;
            }

            Entity player = mc.getRenderViewEntity();
            if (player == null) return;
            IBlockState state = mc.world.getBlockState(pos);
            if (state.getMaterial() == Material.AIR) return;
            float partialTicks = mc.getRenderPartialTicks();
            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;

            int rgb = ColorsModule.INSTANCE.getColor().getRGB();
            RenderUtil.enableGL3D();
            RenderGlobal.drawSelectionBoundingBox(state.getSelectedBoundingBox(mc.world, pos).grow(0.0020000000949949026D).offset(-x, -y, -z),
                    ((rgb >> 16) & 0xFF) / 255F,
                    ((rgb >> 8) & 0xFF) / 255F,
                    (rgb & 0xFF) / 255F, 1);
            RenderUtil.disableGL3D();
        }
    }

}
