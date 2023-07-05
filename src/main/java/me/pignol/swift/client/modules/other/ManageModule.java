package me.pignol.swift.client.modules.other;

import me.pignol.swift.api.util.TextUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class ManageModule extends Module {

    public static ManageModule INSTANCE = new ManageModule();

    public final Value<Settings> settings = new Value<>("Settings", Settings.MISC);

    public final Value<Boolean> safety = new Value<>("Safety", true, v -> settings.getValue() == Settings.SAFETY);
    public final Value<Float> maxDamage = new Value<>("MaxDamage", 6.0F, 0.0F, 36.0F, v -> settings.getValue() == Settings.SAFETY);
    public final Value<Float> crystalRange = new Value<>("CrystalCheckRange", 8.0F, 1.0F, 26.0F, v -> settings.getValue() == Settings.SAFETY);

    public final Value<Boolean> friends = new Value<>("Friends", true, v -> settings.getValue() == Settings.MISC);

    private final Value<Integer> fov = new Value<>("FOV", 110, 100, 200, v -> settings.getValue() == Settings.MISC);

    public final Value<Integer> serverNotResponding = new Value<>("ServerNotResponding", 2500, 500, 5000, v -> settings.getValue() == Settings.MISC);

    public final Value<Integer> holeRange = new Value<>("HoleRange", 6, 0, 42, v -> settings.getValue() == Settings.MISC);
    public final Value<Integer> holeRangeY = new Value<>("HoleYRange", 6, 0, 42, v -> settings.getValue() == Settings.MISC);
    public final Value<Integer> holeSearchDelay = new Value<>("HoleSearchDelay", 150, 0, 1000, v -> settings.getValue() == Settings.MISC);

    public final Value<Boolean> rotate = new Value<>("Rotate", true, v -> settings.getValue() == Settings.MISC);
    public final Value<Boolean> debugRotations = new Value<>("DebugRotations", true, v -> settings.getValue() == Settings.MISC);

    public final Value<TextUtil.Color> bracketColor = new Value<>("BracketColor", TextUtil.Color.WHITE, v -> settings.getValue() == Settings.MISC);
    public final Value<TextUtil.Color> nameColor = new Value<>("NameColor", TextUtil.Color.DARK_GRAY, v -> settings.getValue() == Settings.MISC);

    public final Value<Integer> tabbedFps = new Value<>("TabbedFPS", 60, 1, 240, v -> settings.getValue() == Settings.MISC);

    public final Value<Boolean> clearTutorial = new Value<>("ClearTutorial", true, v -> settings.getValue() == Settings.MISC);

    public ManageModule() {
        super("Manage", Category.OTHER, true);
        setDrawn(false);
    }

    @Override
    public void onDisable() {
        setEnabled(true);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        mc.gameSettings.fovSetting = fov.getValue();
        if (clearTutorial.getValue()) {
            mc.gameSettings.tutorialStep = TutorialSteps.NONE;
            mc.getTutorial().setStep(TutorialSteps.NONE);
            clearTutorial.setValue(false);
        }
    }

    public enum Settings {
        SAFETY,
        MISC
    }

}

