package me.pignol.swift.client.blowbui.glowclient.clickgui;

import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.blowbui.glowclient.clickgui.buttons.*;
import me.pignol.swift.client.blowbui.glowclient.clickgui.utils.ColorUtils;
import me.pignol.swift.client.blowbui.glowclient.utils.render.Colors;
import me.pignol.swift.client.blowbui.glowclient.utils.render.SurfaceBuilder;
import me.pignol.swift.client.blowbui.glowclient.utils.render.SurfaceHelper;
import me.pignol.swift.client.managers.ModuleManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ClickGuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Window extends BaseButton {

    public boolean isOpen = true;

    private final List<BaseButton> buttons = new ArrayList<>();
    private final Category category;
    private final String text;

    private boolean isDragging = false;
    private int modulesCounted = 0;
    private int scroll = 0;
    private int renderYButton = 0;
    private int dragX = 0;
    private int dragY = 0;

    public Window(int x, int y, String name, Category category) {
        super(x, y, 100, 12);
        this.text = name;
        this.category = category;
    }

    public void processMouseClick(int mouseX, int mouseY, int click) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (this.isMouseHovered()) {
            if (click == 0) {
                this.isDragging = true;
                this.dragX = mouseX - this.getX();
                this.dragY = mouseY - this.getY();
            } else if (click == 1) {
                this.isOpen = !this.isOpen;
            }
        }

        for (BaseButton button : this.buttons) {
            if (button.shouldRender()) {
                button.processMouseClick(mouseX, mouseY, click);
            }
        }
    }

    public void processMouseRelease(int mouseX, int mouseY, int click) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (this.isDragging) {
            this.isDragging = false;
        }

        for (BaseButton button : buttons) {
            if (button.shouldRender()) {
                button.processMouseRelease(mouseX, mouseY, click);
            }
        }
    }

    public void processKeyPress(char character, int key) {
        for (BaseButton button : buttons) {
            if (button.shouldRender()) {
                button.processKeyPress(character, key);
            }
        }
    }

    public void draw(int mouseX, int mouseY) {
        int red = ClickGuiModule.INSTANCE.red.getValue();
        int green = ClickGuiModule.INSTANCE.green.getValue();
        int blue = ClickGuiModule.INSTANCE.blue.getValue();

        int bgRed = ClickGuiModule.INSTANCE.backgroundRed.getValue();
        int bgGreen = ClickGuiModule.INSTANCE.backgroundGreen.getValue();
        int bgBlue = ClickGuiModule.INSTANCE.backgroundBlue.getValue();
        int bgAlpha = ClickGuiModule.INSTANCE.backgroundAlpha.getValue();
        if (isDragging) {
            setX(mouseX - this.dragX);
            setY(mouseY - this.dragY);
        }

        GL11.glPushMatrix();
        GL11.glPushAttrib(1284);
        this.scroll = Math.max(0, this.scroll);
        this.scroll = Math.min(this.getDisplayableCount() - this.getModulesToDisplay(), this.scroll);
        this.updateIsMouseHovered(mouseX, mouseY);
        this.renderYButton = this.getY();

        SurfaceHelper.drawRect(this.getX(), this.getY(), this.getWidth(), 13, Colors.toRGBA(red, green, blue, ClickGuiModule.INSTANCE.categoryAlpha.getValue()));
        if (this.isOpen) {
            SurfaceHelper.drawRect(this.getX(), this.getY() + 13, this.getWidth(), this.getDisplayedHeight() + 4 - 13, Colors.toRGBA(bgRed, bgGreen, bgBlue, bgAlpha));
        } else {
            SurfaceHelper.drawRect(this.getX(), this.getY() + 13, this.getWidth(), this.getDisplayedHeight() + 1 - 13, Colors.toRGBA(bgRed, bgGreen, bgBlue, bgAlpha));
        }

        GL11.glColor3f(0.0F, 0.0F, 0.0F);
        SurfaceBuilder builder = new SurfaceBuilder();
        if (ClickGuiModule.INSTANCE.customFont.getValue()) {
            builder.reset().task(SurfaceBuilder::enableBlend)
                    .task(SurfaceBuilder::enableFontRendering)
                    .fontRenderer(ClickGUI.fontRenderer)
                    .color(Colors.WHITE)
                    .text(this.text, (double) (this.getX() + 2 + 1), (double) (this.getY() + 2 + 1), true)
                    .color(Colors.WHITE).text(this.text, (double) (this.getX() + 2), (double) (this.getY() + 2));
        } else {
            builder.reset().task(SurfaceBuilder::enableBlend).task(SurfaceBuilder::enableFontRendering).color(Colors.WHITE).text(this.text, (double)(this.getX() + 2), (double)(this.getY() + 2), true);
        }
//      if (ClickGui.fontRenderer != null) {
//         builder.reset().task(SurfaceBuilder::enableBlend).task(SurfaceBuilder::enableFontRendering).fontRenderer(ClickGui.fontRenderer).color(Colors.WHITE).text(this.text, (double)(this.getX() + 2 + 1), (double)(this.getY() + 2 + 1), true).color(Colors.WHITE).text(this.text, (double)(this.getX() + 2), (double)(this.getY() + 2));
//      } else {
//         builder.reset().task(SurfaceBuilder::enableBlend).task(SurfaceBuilder::enableFontRendering).fontRenderer(ClickGui.fontRenderer).color(Colors.WHITE).text(this.text, (double)(this.getX() + 2), (double)(this.getY() + 2), true);
//      }

        if (this.isOpen) {
            int i;
            if (this.shouldScroll()) {
                i = this.getScrollbarHeight();
                int barY = this.getScrollbarY();
                barY = Math.min(barY, (int)((this.getScrollingModuleCount() * ClickGUI.fontRenderer.getHeight() * 2.05) - 1 - i));
                RenderUtil.drawRect((float) (this.getX() + 97), (float) (this.getY() + 13 + barY + 5), (float) (this.getX() + 99), (float) Math.min(this.getY() + 13 + barY + i, this.getY() + this.getDisplayedHeight()), Colors.toRGBA(red, green, blue, 255));
            }

            this.modulesCounted = 0;

            for (i = this.getScroll(); i < this.getModulesToDisplay() + this.getScroll(); ++i) {
                BaseButton but = this.getNextEntry();
                ++this.modulesCounted;
                but.draw(mouseX, mouseY);
            }
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public int getHeight() {
        int i = this.height;

        for (BaseButton button : this.buttons) {
            if (button.shouldRender()) {
                i += ClickGUI.fontRenderer.getHeight() * 2.05;
            }
        }

        return i;
    }

    public void openGui() {
        for (BaseButton button : this.buttons) {
            button.openGui();
        }
    }

    public boolean shouldRender() {
        return true;
    }

    public String getName() {
        return this.text;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public void setOpen(boolean val) {
        this.isOpen = val;
    }

    public int getColor() {
        return ColorUtils.getColorForGuiEntry(1, this.isMouseHovered(), false);
    }

    public void init(Category category) {
        List<Module> modules = ModuleManager.getInstance().getModules().stream().filter(mod -> mod.getCategory() == category).sorted(Comparator.comparing(Module::getName)).collect(Collectors.toList());

        for (Module module : modules) {
            Button b = this.addButton(new Button(this, module));
            if (module.getCategory() != Category.OTHER) {
                this.addSubBind(new SubBind(b));
            }
            for (Value setting : module.getValues()) {
                Object value = setting.getValue();
                if (value.getClass().isEnum()) {
                    this.addSubMode(new SubMode(b, setting));
                } else if (value instanceof Number) {
                    this.addSubSlider(new SubSlider(b, setting));
                } else if (value instanceof Boolean) {
                    this.addSubButton(new SubButton(b, setting));
                }
            }
        }
    }

    private Button addButton(Button b) {
        this.buttons.add(b);
        return b;
    }

    private SubButton addSubButton(SubButton b) {
        this.buttons.add(b);
        b.getParent().getSubEntries().add(b);
        return b;
    }

    private SubBind addSubBind(SubBind b) {
        this.buttons.add(b);
        b.getParent().getSubEntries().add(b);
        return b;
    }

    private SubMode addSubMode(SubMode b) {
        this.buttons.add(b);
        b.getParent().getSubEntries().add(b);
        return b;
    }

    private SubSlider addSubSlider(SubSlider slider) {
        this.buttons.add(slider);
        slider.getParent().getSubEntries().add(slider);
        return slider;
    }

    private BaseButton getNextEntry() {
        int a = 0;
        int i = this.scroll;

        while (true) {
            BaseButton but = this.buttons.get(i);
            if (but.shouldRender()) {
                if (this.modulesCounted == 0 || a >= this.modulesCounted) {
                    return but;
                }

                ++a;
            }

            ++i;
        }
    }

    public int getRenderYButton() {
        return this.renderYButton += ClickGUI.fontRenderer.getHeight() * 2.05;
    }

    private int getDisplayedHeight() {
        int max = this.maxDisplayHeight();
        int normal = this.getHeight();
        return Math.min(max + 14, normal);
    }

    private void updateIsMouseHoveredFull(int mouseX, int mouseY) {
        int x = this.getX();
        int y = this.getY();
        int maxX = x + this.width;
        int maxY = y + this.getDisplayedHeight();
        this.isHoveredCached = x <= mouseX && mouseX <= maxX && y <= mouseY && mouseY <= maxY;
    }

    private int getScrollbarHeight() {
        double maxHeight = (double) this.maxDisplayHeight();
        double maxAllowedModules = (double) this.getScrollingModuleCount();
        double displayable = (double) this.getDisplayableCount();
        return (int) Math.floor(maxHeight * (maxAllowedModules / displayable));
    }

    private int getScrollbarY() {
        int displayable = this.getDisplayableCount();
        int rest = displayable - this.scroll;
        int resultRaw = displayable - rest;
        return (int) (resultRaw * ClickGUI.fontRenderer.getHeight() * 2.05);
    }

    private int getScroll() {
        return this.shouldScroll() ? this.scroll : 1;
    }

    private boolean shouldScroll() {
        return this.getScrollingModuleCount() - 1 < this.getDisplayableCount();
    }

    public void handleScroll(int dWheel, int x, int y) {
        this.updateIsMouseHoveredFull(x, y);
        if (this.isMouseHovered() && this.shouldScroll()) {
            this.scroll += dWheel;
        }
    }

    private int maxDisplayHeight() {
        Minecraft MC = FMLClientHandler.instance().getClient();
        ScaledResolution scaledresolution = new ScaledResolution(MC);
        int height = scaledresolution.getScaledHeight();
        height = Math.floorDiv(height, (int) (ClickGUI.fontRenderer.getHeight() * 2.05));
        --height;
        height *= ClickGUI.fontRenderer.getHeight() * 2.05;
        return height;
    }

    private int getDisplayableCount() {
        int i = 0;

        for (BaseButton but : this.buttons) {
            if (but.shouldRender()) {
                ++i;
            }
        }

        return i;
    }

    private int getScrollingModuleCount() {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int height = scaledresolution.getScaledHeight();
        height = Math.floorDiv(height, (int) (ClickGUI.fontRenderer.getHeight() * 2.05));
        --height;
        return height;
    }

    private int getModulesToDisplay() {
        return this.shouldScroll() ? this.getScrollingModuleCount() : this.getDisplayableCount();
    }

    public Category getCategory() {
        return this.category;
    }

}
