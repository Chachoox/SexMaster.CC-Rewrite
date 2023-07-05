package me.pignol.swift.api.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface AccessorMinecraft {

    @Accessor("timer")
    Timer getTimer();

    @Accessor("rightClickDelayTimer")
    void setDelay(int delay);

}

