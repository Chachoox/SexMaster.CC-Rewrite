package me.pignol.swift.client.command.commands;

import me.pignol.swift.api.util.ChatUtil;
import me.pignol.swift.client.command.Command;
import me.pignol.swift.client.managers.FriendManager;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("Friend", new String[]{"f"});
    }

    @Override
    public void run(String[] args) {
        if (args.length < 2) {
            return;
        }

        switch (args[1].toUpperCase()) {
            case "ADD":
                if (FriendManager.getInstance().isFriend(args[2])) {
                    ChatUtil.printString("That player is a friend already.");
                } else {
                    FriendManager.getInstance().addFriend(args[2]);
                    ChatUtil.printString("Added " + args[2] + " to friends list.");
                }
                break;
            case "DEL":
            case "REMOVE":
            case "DELETE":
                if (FriendManager.getInstance().isFriend(args[2])) {
                    FriendManager.getInstance().removeFriend(args[2]);
                    ChatUtil.printString("Removed " + args[2] + " from friends.");
                } else {
                    ChatUtil.printString("That user isnt a friend.");
                }
                break;
        }
    }

}
