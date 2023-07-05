package me.pignol.swift.client.command.commands;

import me.pignol.swift.api.util.ChatUtil;
import me.pignol.swift.client.command.Command;
import me.pignol.swift.client.managers.ModuleManager;

public class DrawnCommand extends Command {

    public DrawnCommand() {
        super("Drawn", new String[]{});
    }

    @Override
    public void run(String[] args) {
        if (args.length < 1) {
            return;
        }

        try {
            ModuleManager.getInstance().getModules()
                    .stream()
                    .filter(mod -> mod.getName().equalsIgnoreCase(args[1]))
                    .findFirst()
                    .ifPresent(mod -> {
                        mod.setDrawn(!mod.isDrawn());
                        ChatUtil.printString((mod.isDrawn() ? "Unhid" : "Hid") + " " + mod.getName());
                    });
        } catch (Exception ex) {
            ChatUtil.printString(ex.getMessage());
        }
    }

}
