package top.xfunny.mixin;

import org.mtr.mod.client.DynamicTextureCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DynamicTextureCache.class)
public abstract class MixinDynamicTextureCache {
    @Inject(at = @At("TAIL"),
            method = "<init>",
            remap = false)
    private void afterConstruct(CallbackInfo callback) {
        top.xfunny.mod.client.DynamicTextureCache.instance = new top.xfunny.mod.client.DynamicTextureCache();
    }

    @Inject(at = @At("TAIL"),
            method = "tick",
            remap = false)
    private void afterTick(CallbackInfo ci) {
       top.xfunny.mod.client.DynamicTextureCache.instance.tick();
    }

}
