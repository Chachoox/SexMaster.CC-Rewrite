package me.pignol.swift.api.mixins.optimization;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.ObjectIntIdentityMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.List;

@Mixin(ObjectIntIdentityMap.class)
public class MixinObjectIntIdentityMap<T> {

    @Mutable
    @Shadow
    @Final
    protected IdentityHashMap<T, Integer> identityMap;

    @Mutable
    @Shadow
    @Final
    protected List<T> objectList;

    @Inject(method = "<init>(I)V", at = @At("RETURN"), cancellable = true)
    public void onInit(int expectedSize, CallbackInfo ci) {
        ci.cancel();
        this.objectList = new ObjectArrayList<>(expectedSize);
        this.identityMap = new IdentityHashMap<>(expectedSize);
    }

}
