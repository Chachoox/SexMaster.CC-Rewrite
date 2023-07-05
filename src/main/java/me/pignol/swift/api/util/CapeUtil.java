package me.pignol.swift.api.util;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.UUID;

public class CapeUtil {

    public static final HashMap<UUID, ResourceLocation> CAPED_USERS = Maps.newHashMap();

    static {
        ResourceLocation jordo = new ResourceLocation("capes/jordo.png");

        CAPED_USERS.put(UUID.fromString("f9a6b946-7186-463e-b859-633f52e33e9e"), jordo);
        CAPED_USERS.put(UUID.fromString("f2e9ae88-993f-416d-a9bb-0839a26c2e55"), jordo);

        CAPED_USERS.put(UUID.fromString("c137f0cf-5e87-4176-8b82-325916bcb3bd"), new ResourceLocation("capes/proby.png"));

        CAPED_USERS.put(UUID.fromString("527f2230-e557-452a-9188-17dece1842ce"), new ResourceLocation("capes/chard.png"));

        CAPED_USERS.put(UUID.fromString("cc72ff00-a113-48f4-be18-2dda8db52355"), new ResourceLocation("capes/hollow.png"));
    }

}
