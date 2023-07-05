package me.pignol.swift.client.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.mixins.AccessorRenderManager;
import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.managers.FontManager;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.misc.TotemPopCounterModule;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NametagsModule extends Module {

    public static NametagsModule INSTANCE = new NametagsModule();

    public NametagsModule() {
        super("Nametags", Category.RENDER);
    }

    private final Value<Boolean> armor = new Value<>("Armor", true);
    private final Value<Float> size = new Value<>("Size", 6.4f, 0.1f, 20.0f);
    private final Value<Boolean> ping = new Value<>("Ping", true);
    //private final Value<Boolean> totemPops = new Value<>("TotemPops", true);
    private final Value<Boolean> rect = new Value<>("Rectangle", true);
    private final Value<Boolean> showEnchants = new Value<>("ShowEnchants", true);
    private final Value<Boolean> outlinedRect = new Value<>("OutlineRect", true);
    private final Value<Integer> lineWidth = new Value<>("OutlineWidth", 10, 1, 20, v -> outlinedRect.getValue());
    private final Value<Boolean> scaleing = new Value<>("Scale", false);
    private final Value<Float> factor = new Value<>("Factor", 0.5f, 0.1f, 1.0f);
    private final Value<Boolean> smartScale = new Value<>("SmartScale", false);
    private final Value<Boolean> colored = new Value<>("Colored", true);
    private final Value<Boolean> pops = new Value<>("Pops", true);

    private final AccessorRenderManager renderManager = ((AccessorRenderManager) mc.getRenderManager());
    private final ICamera frustum = new Frustum();

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.getRenderViewEntity() == null) return;
        frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player != mc.player && !EntityUtil.isDead(player) && frustum.isBoundingBoxInFrustum(player.getRenderBoundingBox())) {
                double x = interpolate(player.lastTickPosX, player.posX, mc.getRenderPartialTicks()) - renderManager.getRenderPosX();
                double y = interpolate(player.lastTickPosY, player.posY, mc.getRenderPartialTicks()) - renderManager.getRenderPosY();
                double z = interpolate(player.lastTickPosZ, player.posZ, mc.getRenderPartialTicks()) - renderManager.getRenderPosZ();
                renderNameTag(player, x, y, z, mc.getRenderPartialTicks());
            }
        }
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += (player.isSneaking() ? 0.5D : 0.7D);
        Entity camera = mc.getRenderViewEntity();
        assert camera != null;
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);

        String displayTag = getDisplayTag(player);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = FontManager.getInstance().getStringWidth(displayTag) / 2;
        double scale = (0.0018 + size.getValue() * (distance * factor.getValue())) / 1000.0;

        if (distance <= 8 && smartScale.getValue()) {
            scale = 0.0245D;
        }

        if (!scaleing.getValue()) {
            scale = size.getValue() / 100.0;
        }

        GlStateManager.pushMatrix();
        //RenderHelper.enableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();

        if (rect.getValue()) {
            RenderUtil.drawRect(-width - 2, -(mc.fontRenderer.FONT_HEIGHT + 1), width + 2F, 1, 0x55000000);
        }
        if (outlinedRect.getValue()) {
            GlStateManager.glLineWidth(lineWidth.getValue() / 10F);
            RenderUtil.drawOutlineRect(-width - 2, -(mc.fontRenderer.FONT_HEIGHT + 1), width + 2F, 1, getDisplayColour(player, false));
        }

        if (armor.getValue()) {
            GlStateManager.pushMatrix();
            int xOffset = -8;
            xOffset -= 32;

            xOffset -= 8;
            this.renderItemStack(player.getHeldItemOffhand(), xOffset, -26);
            xOffset += 16;

            final NonNullList<ItemStack> armorInventory = player.inventory.armorInventory;
            for (int i = 0; i < 4; i++) {
                this.renderItemStack(armorInventory.get(i), xOffset, -26);
                xOffset += 16;
            }

            this.renderItemStack(player.getHeldItemMainhand(), xOffset, -26);

            GlStateManager.popMatrix();
        }

        GlStateManager.disableDepth();
        FontManager.getInstance().drawStringWithShadow(displayTag, -width, -(8), this.getDisplayColour(player, true));
        GlStateManager.enableDepth();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);

        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();

        mc.getRenderItem().zLevel = -150.0F;

        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, y);

        mc.getRenderItem().zLevel = 0.0F;

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();

        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.disableDepth();
        renderEnchantmentText(stack, x, y);
        GlStateManager.enableDepth();
        GlStateManager.scale(2F, 2F, 2F);

        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 8;

        if (ItemUtil.hasDurability(stack.getItem())) {
            FontManager.getInstance().drawStringWithShadow((int) ItemUtil.getDamageInPercent(stack) + "%", x * 2, enchantmentY, stack.getItem().getRGBDurabilityForDisplay(stack));
            enchantmentY -= 8;
        }

        if (showEnchants.getValue()) {
            NBTTagList enchants = stack.getEnchantmentTagList();
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                short level = enchants.getCompoundTagAt(index).getShort("lvl");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    String encName = enc.isCurse()
                            ? TextFormatting.RED
                            + enc.getTranslatedName(level).substring(0, 1)
                            : enc.getTranslatedName(level).substring(0, 1);
                    encName = encName + level;
                    FontManager.getInstance().drawStringWithShadow(encName, x * 2, enchantmentY, -1);
                    enchantmentY -= 8;
                }
            }
        }
    }

    private String getDisplayTag(EntityPlayer player) {
        float health = EntityUtil.getHealth(player);
        String displayTag;

        String color;
        String name = player.getName();

        if (health > 18) {
            color = ChatFormatting.GREEN.toString();
        } else if (health > 16) {
            color = ChatFormatting.DARK_GREEN.toString();
        } else if (health > 12) {
            color = ChatFormatting.YELLOW.toString();
        } else if (health > 8) {
            color = ChatFormatting.GOLD.toString();
        } else {
            color = ChatFormatting.RED.toString();
        }

        displayTag = name; // Add their name
        displayTag += " " + EntityUtil.getPing(player) + "ms"; // Add some space and their ping
        displayTag += " " + (color + (int) health); // Add some space and their health
        if (pops.getValue() && TotemPopCounterModule.INSTANCE.getPopMap().containsKey(player.getName())) {
            displayTag += ChatFormatting.RESET + " -" + TotemPopCounterModule.INSTANCE.getPopMap().get(player.getName()); // ADD some slop and their bots
        }

        return displayTag;
    }

    private int getDisplayColour(EntityPlayer player, boolean forText) {
        if (FriendManager.getInstance().isFriend(player.getName())) {
            return ColorsModule.INSTANCE.getFriendColor().getRGB();
        }
        if (forText && !colored.getValue()) {
            return 0xFFFFFFFF;
        }
        return ColorsModule.INSTANCE.getColor().getRGB();
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }


}
