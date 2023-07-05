package me.pignol.swift.api.mixins;

import com.mojang.authlib.GameProfile;
import me.pignol.swift.api.interfaces.mixin.IEntityPlayerSP;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.MoveEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.CommandManager;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.managers.SpeedManager;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer implements IEntityPlayerSP {

    @Override
    @Accessor(value = "lastReportedYaw")
    public abstract float getLastReportedYaw();

    @Override
    @Accessor(value = "lastReportedPitch")
    public abstract float getLastReportedPitch();

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer abstractClientPlayer, MoverType type, double x, double y, double z) {
        final MoveEvent event = new MoveEvent(x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            super.move(type, event.getX(), event.getY(), event.getZ());
        }
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;onUpdateWalkingPlayer()V", shift = At.Shift.BEFORE))
    public void walkingPre(CallbackInfo ci) {
        RotationManager.getInstance().updateRotations();
        MinecraftForge.EVENT_BUS.post(new UpdateEvent(Stage.PRE));
        SpeedManager.getInstance().update();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    public void walkingPost(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new UpdateEvent(Stage.POST));
        if (!ManageModule.INSTANCE.debugRotations.getValue()) {
            RotationManager.getInstance().restoreRotations();
        }
        SpeedManager.getInstance().update();
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith(CommandManager.getInstance().getPrefix())) {
            ci.cancel();
            CommandManager.getInstance().onMessage(message);
        }
    }

}
