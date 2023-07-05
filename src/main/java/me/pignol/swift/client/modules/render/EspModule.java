package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.mixins.AccessorRenderManager;
import me.pignol.swift.api.util.InterpolationUtil;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class EspModule extends Module {

    public static EspModule INSTANCE = new EspModule();

    private static final Color END_CHEST_COLOR = new Color(0x727272);
    private static final Color CHEST_COLOR = new Color(0xC0914D);
    private static final Color SHULKER_COLOR = new Color(0x785D8D);

    private final Value<Boolean> bottles = new Value<>("Bottles", true);
    private final Value<Boolean> storages = new Value<>("Storages", true);
    public final Value<Integer> lineWidth = new Value<>("LineWidth", 10, 1, 20);

    public EspModule() {
        super("ESP", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (isNull()) {
            return;
        }
        RenderUtil.enableGL3D(lineWidth.getValue() / 10.0F);
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityExpBottle && bottles.getValue()) {
                Vec3d vec = InterpolationUtil.interpolateEntity(entity, mc.getRenderPartialTicks());
                double posX = vec.x - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosX();
                double posY = vec.y - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosY();
                double posZ = vec.z - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosZ();
                AxisAlignedBB bb = new AxisAlignedBB(0.0, 0.0, 0.0, entity.width, entity.height, entity.width).offset(posX - entity.width / 2, posY, posZ - entity.width / 2);

                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, ColorsModule.INSTANCE.getColor().getRed() / 255.0F, ColorsModule.INSTANCE.getColor().getGreen() / 255.0F, ColorsModule.INSTANCE.getColor().getBlue() / 255.0F, 1);
            }
        }

        if (storages.getValue()) {
            for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
                Color color = getColor(tileEntity);
                if (color != null) {
                    RenderUtil.drawBoundingBox(tileEntity.getPos(), color, true, 0);
                }
            }
        }
        RenderUtil.disableGL3D();
    }

    private Color getColor(TileEntity entity) {
        if (entity instanceof TileEntityEnderChest) {
            return END_CHEST_COLOR;
        } else if (entity instanceof TileEntityChest) {
            return CHEST_COLOR;
        } else if (entity instanceof TileEntityShulkerBox) {
            return SHULKER_COLOR;
        }
        return null;
    }


}
