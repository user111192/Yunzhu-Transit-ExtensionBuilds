package top.xfunny.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.mtr.mapping.holder.*;
import org.mtr.mod.block.BlockLiftButtons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.xfunny.mod.Items;

@Mixin(value =BlockLiftButtons.class, remap = false)
public abstract class MixinBlockLiftButtons {
    @ModifyExpressionValue(
            method = "onUse2",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/mtr/mapping/holder/PlayerEntity;isHolding(Lorg/mtr/mapping/holder/Item;)Z"
            )
    )
    private boolean modifyIsHolding(boolean original, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (original) {
            return true;
        } else {
            return player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get()) ||
                    player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get()) ||
                    player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR.get()) ||
                    player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER.get());
        }
    }
}
