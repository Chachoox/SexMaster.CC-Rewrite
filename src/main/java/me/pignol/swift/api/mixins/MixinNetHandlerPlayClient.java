package me.pignol.swift.api.mixins;

import me.pignol.swift.client.event.events.DisconnectEvent;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Inject(method = "onDisconnect", at = @At("HEAD"), cancellable = true)
    public void onDisconnectHook(ITextComponent reason, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new DisconnectEvent());
    }

}
