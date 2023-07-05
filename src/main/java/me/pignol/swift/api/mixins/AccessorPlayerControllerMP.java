package me.pignol.swift.api.mixins;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerControllerMP.class)
public interface AccessorPlayerControllerMP {

    @Invoker
    void invokeSyncCurrentPlayItem();

    @Accessor("blockHitDelay")
    void setBlockHitDelay(int delay);

}
