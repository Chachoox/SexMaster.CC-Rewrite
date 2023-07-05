package me.pignol.swift.client.managers;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import me.pignol.swift.client.modules.other.ManageModule;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class FriendManager {

    private static final FriendManager INSTANCE = new FriendManager();

    private Set<String> friends = new HashSet<>();
    private File directory;

    public static FriendManager getInstance() {
        return INSTANCE;
    }

    public void load(File directory) {
        this.directory = directory;
        loadFriends();
    }

    public void unload() {
        saveFriends();
    }

    public boolean isFriend(String name) {
        return ManageModule.INSTANCE.friends.getValue() && friends.contains(name);
    }

    public void addFriend(String name) {
        friends.add(name);
    }

    public void removeFriend(String name) {
        friends.remove(name);
    }

    public Set<String> getFriends() {
        return friends;
    }

    public void saveFriends() {
        if (directory.exists()) {
            try (Writer writer = new FileWriter(directory)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(friends));
            } catch (IOException e) {
                directory.delete();
            }
        }
    }

    public void loadFriends() {
        if (!directory.exists()) {
            try {
                directory.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }
        try (FileReader inFile = new FileReader(directory)) {
            friends = new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .fromJson(inFile, new TypeToken<HashSet<String>>(){}.getType());
            if (friends == null) {
                friends = new HashSet<>();
            }
        } catch (Exception ignored) {
        }
    }

}
