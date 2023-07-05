package me.pignol.swift.client.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.other.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Module {

    protected final static Minecraft mc = Minecraft.getMinecraft();

    private final List<Value> values = new ArrayList<>();

    private final String name;
    private final Category category;
    private String suffix = "";

    private int key;

    private boolean enabled, needsListener = true, drawn = true;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public Module(String name, Category category, boolean enabled) {
        this.name = name;
        this.category = category;
        if (enabled) setEnabled(enabled);
    }

    public Module(String name, Category category, boolean enabled, boolean needsListener) {
        this.name = name;
        this.category = category;
        this.needsListener = needsListener;
        if (enabled) setEnabled(enabled);
    }

    public void onEnable(){}
    public void onDisable(){}

    public boolean isEnabled() {
        return enabled;
    }

    public void setSuffix(String suffix) {
        if (!suffix.equals(this.suffix)) {
            HudModule.INSTANCE.setNeedsSort(true);
        }
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
            ChatUtil.printString(getName() + " was " + (isEnabled() ? ChatFormatting.GREEN + "enabled" : ChatFormatting.RED + "disabled"), -hashCode());
            if (needsListener)
                MinecraftForge.EVENT_BUS.register(this);
        } else {
            onDisable();
            ChatUtil.printString(getName() + " was " + (isEnabled() ? ChatFormatting.GREEN + "enabled" : ChatFormatting.RED + "disabled"), -hashCode());

            if (needsListener)
                MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    public boolean isNull() {
        return mc.world == null || mc.player == null;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public List<Value> getValues() {
        return values;
    }

    public void save(JsonObject directory) {
        directory.addProperty("Key", getKey());
        directory.addProperty("Enabled", isEnabled());
        directory.addProperty("Drawn", isDrawn());
        for (Value val : values) {
            directory.addProperty(val.getName(), val.getValue().toString());
        }
    }

    public void load(JsonObject directory) {
        for (Map.Entry<String, JsonElement> entry : directory.entrySet()) {
            switch (entry.getKey()) {
                case "Key":
                    setKey(entry.getValue().getAsInt());
                    continue;
                case "Enabled":
                    if (!(isEnabled() && entry.getValue().getAsBoolean()) && !(!isEnabled() && !entry.getValue().getAsBoolean()))
                        setEnabled(entry.getValue().getAsBoolean());
                    continue;
                case "Drawn":
                    setDrawn(entry.getValue().getAsBoolean());
                    continue;
            }

            for (Value val : values) {
                try {
                    if (val.getName().equalsIgnoreCase(entry.getKey())) {
                        if (val.getValue() instanceof Boolean) {
                            val.setValue(entry.getValue().getAsBoolean());
                        } else if (val.getValue() instanceof Number) {
                            if (val.getValue().getClass() == Float.class) {
                                val.setValue(entry.getValue().getAsFloat());
                            } else if (val.getValue().getClass() == Double.class) {
                                val.setValue(entry.getValue().getAsDouble());
                            } else if (val.getValue().getClass() == Integer.class) {
                                val.setValue(entry.getValue().getAsInt());
                            }
                        } else if (val.getValue() instanceof String) {
                            val.setValue(entry.getValue().getAsString());
                        } else if (val.getValue() instanceof Enum) {
                            val.setEnumValue(entry.getValue().getAsString());
                        }
                    }
                } catch (Exception ex) {
                    val.setValue(val.getDefaultValue());
                }
            }
        }
    }

    public enum BindType {
        TOGGLE,
        HOLD
    }

}
