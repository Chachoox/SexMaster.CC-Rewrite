package me.pignol.swift.client.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.gui.ChatFormatting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.pignol.swift.api.util.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.command.Command;
import me.pignol.swift.client.command.commands.*;
import me.pignol.swift.client.modules.Module;

import java.io.*;

public class CommandManager {

    private static final CommandManager INSTANCE = new CommandManager();

    private final ObjectArrayList<Command> commands = new ObjectArrayList<>();

    private String prefix = ".";
    private File file;

    public static CommandManager getInstance() {
        return INSTANCE;
    }

    public void load(File file) {
        this.file = file;
        commands.add(new BindCommand());
        commands.add(new ToggleCommand());
        commands.add(new CheckValuesCommand());
        commands.add(new DrawnCommand());
        commands.add(new FriendCommand());
        commands.add(new PrefixCommand());
        commands.add(new HistoryCommand());

        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                JsonElement node = new JsonParser().parse(reader);
                if (node.isJsonObject()) {
                    prefix = node.getAsJsonObject()
                            .get("Prefix")
                            .getAsJsonObject()
                            .getAsString();
                }
            } catch (Exception ignored){}
        }
    }

    public void unload() {
        final JsonObject node = new JsonObject();
        node.addProperty("Prefix", prefix);
        if (node.entrySet().isEmpty()) {
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            return;
        }
        try (Writer writer = new FileWriter(file)) {
            writer.write(new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(node)
            );
        } catch (IOException e) {
            file.delete();
        }
    }

    public void onMessage(String message) {
        String[] args = message.substring(1).split(" ");
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(args[0])) {
                command.run(args);
                return;
            }
            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(args[0])) {
                    command.run(args);
                    return;
                }
            }
        }

        for (Module module : ModuleManager.getInstance().getModules()) {
            if (args[0].equalsIgnoreCase(module.getDisplayName())) {
                for (Value value : module.getValues()) {
                    if (value.getName().equalsIgnoreCase(args[1])) {
                        if (value.getValue() instanceof Integer) {
                            Integer valueOf = Integer.valueOf(args[2]);
                            value.setValue(valueOf);
                            ChatUtil.printString("Set " + value.getName() + " in " + module.getName() + " to " + valueOf);
                        } else if (value.getValue() instanceof Float) {
                            Float valueOf = Float.valueOf(args[2]);
                            value.setValue(valueOf);
                            ChatUtil.printString("Set " + value.getName() + " in " + module.getName() + " to " + valueOf);
                        } else if (value.getValue() instanceof Boolean) {
                            Boolean valueOf = Boolean.valueOf(args[2]);
                            value.setValue(valueOf);
                            ChatUtil.printString("Set " + value.getName() + " in " + module.getName() + " to " + valueOf);;
                        }
                        return;
                    }
                }
            }
        }

        ChatUtil.printString(ChatFormatting.RED + "Cant find a command named " + args[0]);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
