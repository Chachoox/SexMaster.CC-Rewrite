package me.pignol.swift.api.mixins.optimization;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VertexFormat.class)
public class MixinVertexFormat {

    @Mutable
    @Final
    @Shadow
    private List<VertexFormatElement> elements;

    @Mutable
    @Final
    @Shadow
    private List<Integer> offsets;

    @Mutable
    @Final
    @Shadow
    private List<Integer> uvOffsetsById;

    @Shadow
    private int colorElementOffset;

    @Shadow
    private int normalElementOffset;

    @Inject(method = "<init>()V", at = @At("RETURN"), cancellable = true)
    public void init(CallbackInfo ci) {
        ci.cancel();
        this.elements = Lists.newArrayList();
        this.offsets = new IntArrayList();
        this.colorElementOffset = -1;
        this.uvOffsetsById = new IntArrayList();
        this.normalElementOffset = -1;
    }

}
