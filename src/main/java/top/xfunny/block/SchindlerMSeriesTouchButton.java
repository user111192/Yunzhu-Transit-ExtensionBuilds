package top.xfunny.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.BlockEntityTypes;
import top.xfunny.block.base.LiftButtonsBase;

import javax.annotation.Nonnull;
import java.util.List;

public class SchindlerMSeriesTouchButton extends LiftButtonsBase {
    public SchindlerMSeriesTouchButton()
    {
        super();
    }
    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(5, 0, 0, 11, 8, 0.1, IBlock.getStatePropertySafe(state, FACING));
    }
    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SchindlerMSeriesTouchButton.BlockEntity(blockPos, blockState);
    }
    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(UNLOCKED);
        properties.add(SINGLE);
    }
    public static class BlockEntity extends BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.SCHINDLER_M_SERIES_TOUCH_BUTTON.get(), pos, state, false);
        }
    }
}

