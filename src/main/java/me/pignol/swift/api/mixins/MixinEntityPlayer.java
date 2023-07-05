package me.pignol.swift.api.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;playShoulderEntityAmbientSound(Lnet/minecraft/nbt/NBTTagCompound;)V"), cancellable = true)
    public void playAmbient(CallbackInfo ci) {
        ci.cancel();
    }

}
