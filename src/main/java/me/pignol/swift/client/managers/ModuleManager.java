package me.pignol.swift.client.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.KeyPressEvent;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.combat.*;
import me.pignol.swift.client.modules.misc.*;
import me.pignol.swift.client.modules.movement.*;
import me.pignol.swift.client.modules.other.*;
import me.pignol.swift.client.modules.player.*;
import me.pignol.swift.client.modules.render.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.lang.reflect.Field;

public class ModuleManager {

    private static final ModuleManager INSTANCE = new ModuleManager();

    private final ObjectArrayList<Module> modules = new ObjectArrayList<>();

    private File directory;

    public static ModuleManager getInstance() {
        return INSTANCE;
    }

    public void load(File file) {
        this.directory = file;

        MinecraftForge.EVENT_BUS.register(this);

        //OTHER
        addModule(FontModule.INSTANCE);
        addModule(ManageModule.INSTANCE);
        addModule(ColorsModule.INSTANCE);
        addModule(HudModule.INSTANCE);
        addModule(ClickGuiModule.INSTANCE);

        //COMBAT
        addModule(AuraModule.INSTANCE);
        addModule(new AutoCrystalModule());
        addModule(new InstantFloorModule());
        addModule(new SelfFillModule());
        addModule(new SurroundModule());
        addModule(new OffhandModule());
        addModule(new AutoTrapModule());
        addModule(new HoleFillerModule());
        addModule(new CriticalsModule());

        //RENDER
        addModule(ChamsModule.INSTANCE);
        addModule(SkeletonModule.INSTANCE);
        addModule(NoRenderModule.INSTANCE);
        addModule(CustomSkyModule.INSTANCE);
        addModule(NametagsModule.INSTANCE);
        addModule(EnchantGlintModule.INSTANCE);
        addModule(ViewmodelModule.INSTANCE);
        addModule(BlockHighlightModule.INSTANCE);
        addModule(EspModule.INSTANCE);
        addModule(new FullBrightModule());
        addModule(new TracersModule());
        addModule(new HoleEspModule());
        addModule(new LogoutSpotsModule());

        //PLAYER
        addModule(new FastBreakModule());
        addModule(new AutoStackFillModule());
        addModule(new ExpTweaksModule());
        addModule(new PingSpike());
        addModule(new AntiAim());

        //MOVEMENT
        addModule(new VelocityModule());
        addModule(new StepModule());
        addModule(new SpeedModule());
        addModule(new FastDropModule());
        addModule(new Clip());

        //MISC
        addModule(TotemPopCounterModule.INSTANCE);
        addModule(ShulkerPreviewModule.INSTANCE);
        addModule(BetterTabModule.INSTANCE);
        addModule(new PearlTraceModule());
        addModule(new Dot5Module());
        addModule(new PingSpoofModule());
        addModule(new PacketCancellerModule());
        addModule(new ChatTimestampsModule());
        addModule(new VisualRangeModule());
        addModule(new AutoGgModule());
        addModule(new NoQuitDesyncModule());
        addModule(new PayloadSpoofModule());
        addModule(new FakePlayerModule());

        HudModule.INSTANCE.setupModules();
        HudModule.INSTANCE.sortModules();
        loadModules();
    }

    @SubscribeEvent
    public void onKeyPress(KeyPressEvent event) {
        for (Module module : modules) {
            if (event.getState() && module.getKey() == event.getKey()) {
                module.toggle();
            }
        }
    }

    public void addModule(Module module) {
        try {
            for (Field field : module.getClass().getDeclaredFields()) {
                if (Value.class.isAssignableFrom(field.getType())) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    module.getValues().add((Value) field.get(module));
                }
            }
            modules.add(module);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void unload() {
        saveModules();
    }

    public void saveModules() {
        if (modules.isEmpty()) {
            directory.delete();
        }
        File[] files = directory.listFiles();
        if (!directory.exists()) {
            directory.mkdir();
        } else if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        modules.forEach(module -> {
            File file = new File(directory, module.getName() + ".json");
            JsonObject node = new JsonObject();
            module.save(node);
            if (node.entrySet().isEmpty()) {
                return;
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                return;
            }
            try (Writer writer = new FileWriter(file)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(node));
            } catch (IOException e) {
                file.delete();
            }
        });
        files = directory.listFiles();
        if (files == null || files.length == 0) {
            directory.delete();
        }
    }

    public void loadModules() {
        modules.forEach(module -> {
            final File file = new File(directory, module.getName() + ".json");
            if (!file.exists()) {
                return;
            }
            try (Reader reader = new FileReader(file)) {
                JsonElement node = new JsonParser().parse(reader);
                if (!node.isJsonObject()) {
                    return;
                }
                module.load(node.getAsJsonObject());
            } catch (IOException e) {
            }
        });
    }

    public ObjectArrayList<Module> getModules() {
        return modules;
    }
}
