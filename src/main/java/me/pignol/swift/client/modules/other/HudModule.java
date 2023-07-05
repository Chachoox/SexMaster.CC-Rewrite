package me.pignol.swift.client.modules.other;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.Swift;
import me.pignol.swift.api.util.*;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.blowbui.glowclient.utils.render.Colors;
import me.pignol.swift.client.event.events.Render2DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.FontManager;
import me.pignol.swift.client.managers.ModuleManager;
import me.pignol.swift.client.managers.ServerManager;
import me.pignol.swift.client.managers.SpeedManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.combat.AuraModule;
import me.pignol.swift.client.modules.combat.AutoCrystalModule;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class HudModule extends Module {

    public static HudModule INSTANCE = new HudModule();

    private final Value<Integer> alpha = new Value<>("Alpha", 255, 0, 255);
    private final Value<Integer> ySpace = new Value<>("YSpace", 10, 0, 20);
    private final Value<Integer> sortDelay = new Value<>("SortDelay", 1000, 0, 10000);
    private final Value<Boolean> watermark = new Value<>("Watermark", true);
    private final Value<Boolean> watermarkOffset = new Value<>("WatermarkOffset", false);
    private final Value<Boolean> watermark2 = new Value<>("Watermark2", true);
    private final Value<Integer> watermark2y = new Value<>("Watermark2Y", 100, 0, 600, v -> watermark2.getValue());
    private final Value<Boolean> arraylist = new Value<>("Arraylist", true);
    private final Value<Boolean> suffixSpace = new Value<>("SuffixSpace", true);
    private final Value<Boolean> suffixWhite = new Value<>("SuffixWhite", true);


    private final Value<Integer> animationSpeed = new Value<>("AnimationSpeed", 500, 0, 2000);
    private final Value<Boolean> ping = new Value<>("Ping", true);
    private final Value<Boolean> coords = new Value<>("Coords", true);
    private final Value<Boolean> speed = new Value<>("Speed", true);
    private final Value<Boolean> armor = new Value<>("Armor", true);
    private final Value<Boolean> potionEffects = new Value<>("PotionEffects", true);
    private final Value<Boolean> lag = new Value<>("Lag", true);
    private final Value<Boolean> tps = new Value<>("TPS", true);
    private final Value<Boolean> fps = new Value<>("FPS", true);
    private final Value<Boolean> futureColor = new Value<>("FutureColor", true);
    private final Value<Boolean> pvpInfo = new Value<>("PvpInfo", true);
    private final Value<Boolean> pvpInfoPlr = new Value<>("PvpInfoPlr", true);
    private final Value<Integer> pvpInfoX = new Value<>("PvpInfoX", 2, 0, 1000);
    private final Value<Integer> pvpInfoY = new Value<>("PvpInfoY", 300, 0, 1000);
    public final Value<Boolean> hotbarKeys = new Value<>("HotbarKeys", true);
    private final Value<Rendering> rendering = new Value<>("Rendering", Rendering.UP);
    public final Value<Boolean> hideEffects = new Value<>("HideEffects", true);
    public final Value<Boolean> welcomer = new Value<>("Welcomer", true);
    public final Value<Boolean> compass = new Value<>("Compass", true);
    public final Value<Double> compassScale = new Value<>("CompassScale", 3.0D, 1.0D, 10.0D, v -> compass.getValue());
    public final Value<Boolean> compassSync = new Value<>("CompassSync", true);
    public final Value<Boolean> compassCoord = new Value<>("CompassCoord", true);

    private final TimerUtil sortTimer = new TimerUtil();
    private final HashMap<Module, Float> extendedAmount = new HashMap<>();

    private boolean isTargetTrapped, targetExists;
    private boolean isTargetInRange;
    private boolean isTargetInPlaceRange;
    private boolean needsSort;

    public List<Module> modules;

    public HudModule() {
        super("HUD", Category.OTHER, true);
        setDrawn(false);
    }

    public void setupModules() {
        modules = new ArrayList<>(ModuleManager.getInstance().getModules());
    }

    public void sortModules() {
        modules.sort(Comparator.comparing(mod -> -FontManager.getInstance().getStringWidth(mod.getDisplayName() + (mod.getSuffix().length() == 0 ? "" : ((suffixSpace.getValue() ? " " : "") + (suffixWhite.getValue() ? "\u00a7f" : "\u00a77") + "[" + "\u00a7f" + mod.getSuffix() + (suffixWhite.getValue() ? "" : "\u00a77") + "]")))));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUpdate(UpdateEvent event) {
        if (pvpInfo.getValue()) {
            targetExists = false;

            isTargetInRange = AuraModule.INSTANCE.getLastTarget() != null && AuraModule.INSTANCE.isValidTarget(AuraModule.INSTANCE.getLastTarget());

            EntityPlayer player = EntityUtil.getClosestPlayer(13);
            if (player == null) {

                targetExists = false;
                isTargetInPlaceRange = false;
                //isTargetTrapped = false;
                return;
            } else {
                isTargetInPlaceRange = AutoCrystalModule.INSTANCE.isEnabled() && AutoCrystalModule.INSTANCE.placeRange.getValue() > mc.player.getDistance(player);
            }

            targetExists = true;
            List<Vec3d> poses = BlockUtil.getOffsetList(1, false);
            poses.addAll(BlockUtil.getOffsetList(0, false));
            poses.add(new Vec3d(0, 2, 0));

            isTargetTrapped = true;

            for (Vec3d vec : poses) {
                Block block = mc.world.getBlockState(new BlockPos(player).add(vec.x, vec.y, vec.z)).getBlock();
                if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) {
                    isTargetTrapped = false;
                }
            }
        }
    }

    public static int changeAlpha(int origColor, int userInputedAlpha) {
        origColor = origColor & 0x00ffffff; //drop the previous alpha value
        return (userInputedAlpha << 24) | origColor; //add the one the user inputted
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        ScaledResolution resolution = new ScaledResolution(mc);
        int color = ColorsModule.INSTANCE.getColor().getRGB();
        color = changeAlpha(color, alpha.getValue());

        if (needsSort && sortTimer.hasReached(sortDelay.getValue())) {
            sortModules();
            sortTimer.reset();
        }

        if (watermark.getValue()) {
            FontManager.getInstance().drawStringWithShadow(Swift.NAME_VERSION, 2, 2 + (watermarkOffset.getValue() ? 10 : 0), color);
        }

        if (watermark2.getValue()) {
            mc.fontRenderer.drawStringWithShadow("trollgod.cc", 2, watermark2y.getValue(), -1);
        }

        if (welcomer.getValue()) {
            String welcomerString = "Hello " + mc.player.getName() + " :^)";
            FontManager.getInstance().drawStringWithShadow(welcomerString, (resolution.getScaledWidth() / 2F) - (FontManager.getInstance().getStringWidth(welcomerString) / 2F) + 2, 2, color);
        }

        if (lag.getValue()) {
            if (ServerManager.getInstance().isServerNotResponding()) {
                String lagString = "Server hasn't responded in " + String.format("%.2f", (ServerManager.getInstance().getTimer().getTimePassed() / 1000f)) + "s";
                FontManager.getInstance().drawStringWithShadow(lagString, (resolution.getScaledWidth() / 2F) - (FontManager.getInstance().getStringWidth(lagString) / 2F) + 2, welcomer.getValue() ? 12 : 2, color);
            }
        }

        if (pvpInfo.getValue()) {
            int offsetY = 0;

            if (pvpInfoPlr.getValue()) {
                FontManager.getInstance().drawStringWithShadow("PLR", pvpInfoX.getValue(), pvpInfoY.getValue(), isTargetInPlaceRange ? Colors.GREEN : Colors.RED);
                offsetY += 10;
            }

            FontManager.getInstance().drawStringWithShadow("HTR", pvpInfoX.getValue(), pvpInfoY.getValue() + offsetY, isTargetInRange ? Colors.GREEN : Colors.RED);
            offsetY += 10;

            final int totemCount = ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING);
            FontManager.getInstance().drawStringWithShadow(totemCount + "", pvpInfoX.getValue(), pvpInfoY.getValue() + offsetY, totemCount == 0 ? Colors.RED : Colors.GREEN);
            offsetY += 10;

            final int ping = EntityUtil.getPing(mc.player);
            FontManager.getInstance().drawStringWithShadow("PING " + ping, pvpInfoX.getValue(), pvpInfoY.getValue() + offsetY, ping > 100 ? Colors.RED : Colors.GREEN);
            offsetY += 10;

            if (targetExists) {
                FontManager.getInstance().drawStringWithShadow("LBY", pvpInfoX.getValue(), pvpInfoY.getValue() + offsetY, isTargetTrapped ? Colors.GREEN : Colors.RED);
            }
        }

        boolean renderingUp = rendering.getValue() == Rendering.UP;
        boolean chatOpened = mc.ingameGUI.getChatGUI().getChatOpen();
        if (arraylist.getValue()) {
            float restore = 0F;
            float speedRatio = (animationSpeed.getValue() / (float) Minecraft.getDebugFPS());
            int offset = renderingUp ? 2 : resolution.getScaledHeight() - (chatOpened ? 24 : 10);
            for (int i = 0, modulesSize = modules.size(); i < modulesSize; i++) {
                final Module module = modules.get(i);
                extendedAmount.putIfAbsent(module, restore);
                float extended = extendedAmount.get(module);
                if ((module.isEnabled() || extended > restore) && module.isDrawn()) {
                    String suffix = (module.getSuffix().length() == 0 ? "" : ((suffixSpace.getValue() ? " " : "") + (suffixWhite.getValue() ? "\u00a7f" : "\u00a77") + "[" + "\u00a7f" + module.getSuffix() + (suffixWhite.getValue() ? "" : "\u00a77") + "]"));

                    String nameAndLabel = module.getDisplayName() + suffix;

                    float openingTarget = FontManager.getInstance().getStringWidth(nameAndLabel);
                    float target = module.isEnabled() ? openingTarget : restore;
                    float newAmount = extended;

                    newAmount += 0.8 * speedRatio * (module.isEnabled() ? 1 : -1);

                    newAmount = module.isEnabled() ? Math.min(target, newAmount) : Math.max(target, newAmount);

                    if (!module.isEnabled() && newAmount < 0) {
                        newAmount = restore;
                    }

                    if (module.isEnabled() && target - newAmount < 1) {
                        newAmount = target;
                    }

                    float percent = newAmount / openingTarget;
                    extendedAmount.put(module, newAmount);

                    FontManager.getInstance().drawStringWithShadow(nameAndLabel, resolution.getScaledWidth() - extended - 2, offset, color);
                    offset += (renderingUp ? 10 : -10) * percent;
                }
            }
        }

        if (armor.getValue()) {
            GlStateManager.enableTexture2D();
            final int width = resolution.getScaledWidth() >> 1; // Evil Bit Hack
            final int height = resolution.getScaledHeight() - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            for (int index = 0; index < 4; ++index) {
                final ItemStack is = mc.player.inventory.armorInventory.get(index);
                if (is.isEmpty()) continue;
                final int x = width - 90 + (9 - index - 1) * 20 + 2;
                if (armor.getValue()) {
                    GlStateManager.enableDepth();
                    mc.getRenderItem().zLevel = 200.0f;
                    mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, height);
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, is, x, height, "");
                    mc.getRenderItem().zLevel = 0.0f;
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                }
                final int dmg = (int) ItemUtil.getDamageInPercent(is);
                FontManager.getInstance().drawStringWithShadow(dmg + "", x + 8 - (FontManager.getInstance().getStringWidth(dmg + "") / 2f), height - 8, is.getItem().getRGBDurabilityForDisplay(is));
            }
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }

        int offset = renderingUp ? (chatOpened ? 24 : 10) : 2;
        String gray = futureColor.getValue() ? ChatFormatting.GRAY.toString() : "";

        if (potionEffects.getValue()) {
            for (PotionEffect effect :  mc.player.getActivePotionEffects()) {
                int amplifier = effect.getAmplifier();
                String potionString = I18n.format(effect.getEffectName()) + (amplifier > 0 ? (" " + (amplifier + 1) + "") : "") + ": " + ChatFormatting.WHITE + Potion.getPotionDurationString(effect, 1);
                FontManager.getInstance().drawStringWithShadow(potionString, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(potionString) - 2, renderingUp ? resolution.getScaledHeight() - offset : offset, changeAlpha(effect.getPotion().getLiquidColor(), alpha.getValue()));
                offset += ySpace.getValue();
            }
        }

        if (speed.getValue()) {
            String speedString = gray + "Speed: \u00a7f" + String.format("%.2f", SpeedManager.getInstance().getSpeedKpH()) + "km/h";
            FontManager.getInstance().drawStringWithShadow(speedString, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(speedString) - 2, renderingUp ? resolution.getScaledHeight() - offset : offset, color);
            offset += ySpace.getValue();
        }

        if (tps.getValue()) {
            String tpsString = gray + "TPS: \u00a7f" + String.format("%.2f", ServerManager.getInstance().getTPS());
            FontManager.getInstance().drawStringWithShadow(tpsString, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(tpsString) - 2, renderingUp ? resolution.getScaledHeight() - offset : offset, color);
            offset += ySpace.getValue();
        }

        if (fps.getValue()) {
            String pingString = gray +  "FPS: \u00a7f" + Minecraft.getDebugFPS();
            FontManager.getInstance().drawStringWithShadow(pingString, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(pingString) - 2, renderingUp ? resolution.getScaledHeight() - offset : offset, color);
            offset += ySpace.getValue();
        }

        if (ping.getValue()) {
            String pingString = gray + "Ping: \u00a7f" + EntityUtil.getPing(mc.player) + "ms";
            FontManager.getInstance().drawStringWithShadow(pingString, resolution.getScaledWidth() - FontManager.getInstance().getStringWidth(pingString) - 2, renderingUp ? resolution.getScaledHeight() - offset : offset, color);
            offset += ySpace.getValue();
        }

        if (coords.getValue()) {
            String coordsString = "XYZ: \u00a7f" + getRoundedDouble(mc.player.posX) + "\u00a77,\u00a7f " + getRoundedDouble(mc.player.posY) + "\u00a77,\u00a7f " + getRoundedDouble(mc.player.posZ) + " " + "\u00a77[\u00a7f" + DirectionUtil.convertToCoords(mc.player.getHorizontalFacing()) + "\u00a77]";
            FontManager.getInstance().drawStringWithShadow(coordsString, 2, resolution.getScaledHeight() - (chatOpened ? 24 : 10), color);
        }

        if (compass.getValue() && mc.getRenderViewEntity() != null) {
            final double centerX = resolution.getScaledWidth_double() / 2;
            final double centerY = resolution.getScaledHeight_double() * 0.8;

            for (DirectionUtil.Direction dir : DirectionUtil.Direction.values()) {
                double rad = getPosOnCompass(dir);
                FontManager.getInstance().drawStringWithShadow(
                        compassCoord.getValue() ? DirectionUtil.convertToCoords(dir) : dir.name(),
                        (float) (centerX + getX(rad)),
                        (float) (centerY + getY(rad)),
                        compassSync.getValue() ? ColorsModule.INSTANCE.getColor().getRGB() : (dir == DirectionUtil.Direction.N ? Colors.RED : Colors.WHITE)
                );
            }
        }
    }

    private double getX(double rad) {
        return Math.sin(rad) * (compassScale.getValue() * 10);
    }

    private double getY(double rad) {
        final double epicPitch = MathHelper.clamp(mc.getRenderViewEntity().rotationPitch + 30f, -90f, 90f);
        final double pitchRadians = Math.toRadians(epicPitch); // player pitch
        return Math.cos(rad) * Math.sin(pitchRadians) * (compassScale.getValue() * 10);
    }

    // return the position on the circle in radians
    private static double getPosOnCompass(DirectionUtil.Direction dir) {
        double yaw = Math.toRadians(MathHelper.wrapDegrees(mc.getRenderViewEntity().rotationYaw)); // player yaw
        int index = dir.ordinal();
        return yaw + (index * (Math.PI / 2));
    }

    private String getRoundedDouble(double pos) {
        return String.format("%.2f", pos);
    }

    @SubscribeEvent
    public void onHotbarRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (HudModule.INSTANCE.hotbarKeys.getValue()) {
                int x = event.getResolution().getScaledWidth() / 2 - 87;
                int y = event.getResolution().getScaledHeight() - 18;

                int length = mc.gameSettings.keyBindsHotbar.length;
                for (int i = 0; i < length; i++) {
                    mc.fontRenderer.drawStringWithShadow(mc.gameSettings.keyBindsHotbar[i].getDisplayName(), x + i * 20, y, -1);
                }
            }
        }
    }

    public void setNeedsSort(boolean needsSort) {
        this.needsSort = needsSort;
    }

    public enum Rendering {
        UP,
        DOWN
    }

}
