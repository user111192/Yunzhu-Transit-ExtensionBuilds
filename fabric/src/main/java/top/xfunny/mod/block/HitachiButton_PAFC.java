package top.xfunny.mod.block;

import org.jetbrains.annotations.NotNull;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.mod.BlockEntityTypes;
import top.xfunny.mod.block.base.LiftButtonsBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class HitachiButton_PAFC extends LiftButtonsBase {
    public HitachiButton_PAFC() {
        super(true, true);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(6.4, 0, 0, 9.6, 5.5, 0.1, IBlock.getStatePropertySafe(state, FACING));
    }

    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new HitachiButton_PAFC.BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(UNLOCKED);
        properties.add(SINGLE);
    }

    @Override
    public void addTooltips(@NotNull ItemStack stack, @Nullable BlockView world, List<MutableText> tooltip, @NotNull TooltipContext options) {
        tooltip.add(TextHelper.translatable("tooltip.warning_block_testing").formatted(TextFormatting.RED));
    }

    public static class BlockEntity extends LiftButtonsBase.BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.HITACHI_BUTTON_PAFC.get(), pos, state);
        }
    }
}
