package me.pignol.swift.api.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.EntityDataManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class MixinEntity {

    @Shadow
    protected EntityDataManager dataManager;

}
