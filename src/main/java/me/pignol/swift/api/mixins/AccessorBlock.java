package me.pignol.swift.api.mixins;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface AccessorBlock {

    @Accessor("blockResistance")
    float getBlockResistance();

}
