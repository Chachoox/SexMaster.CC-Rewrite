package me.pignol.swift.api.mixins;

import me.pignol.swift.client.event.events.DeathEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase extends MixinEntity {

    @Shadow
    @Final
    private static DataParameter<Float> HEALTH;

    @Inject(method = "notifyDataManagerChange", at = @At("HEAD"))
    public void notifyDataManagerChangeHook(DataParameter<?> key, CallbackInfo info) {
        if (key.equals(HEALTH)) {
            if (dataManager.get(HEALTH) <= 0.0) {
                MinecraftForge.EVENT_BUS.post(new DeathEvent(EntityLivingBase.class.cast(this)));
            }
        }
    }

}
