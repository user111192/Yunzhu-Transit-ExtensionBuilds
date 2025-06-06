package top.xfunny.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = org.mtr.core.generated.data.LiftSchema.class, remap = false)
public interface MixinLiftSchema {
    @Accessor("speed")
    double getSpeed();
}