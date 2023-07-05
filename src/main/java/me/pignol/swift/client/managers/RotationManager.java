package me.pignol.swift.client.managers;

import net.minecraft.client.Minecraft;

public class RotationManager {

    private static final RotationManager INSTANCE = new RotationManager();

    public static RotationManager getInstance() {
        return INSTANCE;
    }

    private final Minecraft mc = Minecraft.getMinecraft();

    private float yaw, pitch;

    public void updateRotations() {
        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
    }

    public void restoreRotations() {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public void setPlayerRotations(float yaw, float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }


}
