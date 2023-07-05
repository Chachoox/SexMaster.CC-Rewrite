package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.util.Pair;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.managers.HoleManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleEspModule extends Module {

    private final Value<Integer> height = new Value<>("Height", 50, 0, 100, "Hole height");
    private final Value<Boolean> down = new Value<>("Down", false, "Renders holes down");
    private final Value<Boolean> outline = new Value<>("Outline", true);
    private final Value<Boolean> wireframe = new Value<>("Wireframe", true);
    private final Value<Boolean> wireframeTop = new Value<>("WireframeTop", true, v -> wireframe.getValue());
    private final Value<Boolean> ignoreOwn = new Value<>("IgnoreOwn", true);
    //private final Value<Boolean> inView = new Value<>("InView", true);

    private final Value<Integer> obsidianRed = new Value<>("ObsidianRed", 255, 0, 255);
    private final Value<Integer> obsidianGreen = new Value<>("ObsidianGreen", 0, 0, 255);
    private final Value<Integer> obsidianBlue = new Value<>("ObsidianBlue", 0, 0, 255);

    private final Value<Integer> bedrockRed = new Value<>("BedrockRed", 0, 0, 255);
    private final Value<Integer> bedrockGreen = new Value<>("BedrockGreen", 255, 0, 255);
    private final Value<Integer> bedrockBlue = new Value<>("BedrockBlue", 0, 0, 255);

    private final Value<Integer> alpha = new Value<>("Alpha", 50, 0, 255);
    private final Value<Integer> lineWidth = new Value<>("LineWidth", 10, 1, 20);

    public static ICamera camera = new Frustum();

    public HoleEspModule() {
        super("HoleESP", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        RenderUtil.enableGL3D(lineWidth.getValue() / 10F);
        //camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        //MutableAABB bb = new MutableAABB(0, 0, 0, 0, 0, 0);
        for (Pair<BlockPos, Boolean> pair : HoleManager.getInstance().getHoles()) {
            final BlockPos hole = pair.getKey();

            if (ignoreOwn.getValue() && (hole.getX() == Math.floor(mc.player.posX) && hole.getY() == Math.floor(mc.player.posY) && hole.getZ() == Math.floor(mc.player.posZ))) {
                continue;
            }

            final boolean safe = pair.getValue();
            AxisAlignedBB bb = new AxisAlignedBB(hole.getX() - mc.getRenderManager().viewerPosX, hole.getY() - (down.getValue() ? 1 : 0) - mc.getRenderManager().viewerPosY, hole.getZ() - mc.getRenderManager().viewerPosZ, hole.getX() + 1 - mc.getRenderManager().viewerPosX, hole.getY() - (down.getValue() ? 1 : 0) + (height.getValue() / 100F) - mc.getRenderManager().viewerPosY, hole.getZ() + 1 - mc.getRenderManager().viewerPosZ);

            float red = safe ? bedrockRed.getValue() : obsidianRed.getValue();
            float green = safe ? bedrockGreen.getValue() : obsidianGreen.getValue();
            float blue = safe ? bedrockBlue.getValue() : obsidianBlue.getValue();

            if (alpha.getValue() > 0) {
                RenderGlobal.renderFilledBox(bb, red / 255, green / 255f, blue / 255, alpha.getValue() / 255F);
            }
            if (outline.getValue()) {
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, red / 255, green / 255f, blue / 255, 1);
            }
            if (wireframe.getValue()) {
                RenderUtil.drawWireframeBox(bb, red / 255, green / 255, blue / 255, wireframeTop.getValue());
            }
        }
        RenderUtil.disableGL3D();
    }


}
