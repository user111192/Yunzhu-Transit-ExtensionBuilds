package top.xfunny.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.BlockEntityTypes;
import top.xfunny.block.base.LiftHallLanternsBase;
import top.xfunny.data.BlockProperties;

import javax.annotation.Nonnull;
import java.util.List;

import static top.xfunny.block.behavior.HorizontalDoubleBlockBehavior.IS_LEFT;

public class SchindlerMSeriesScreen2Even extends LiftHallLanternsBase {
    public SchindlerMSeriesScreen2Even() {
        super(true,false,true);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if(IBlock.getStatePropertySafe(state, IS_LEFT)) {
            return IBlock.getVoxelShapeByDirection(-1, 7, 0, 16, 12, 0.1, IBlock.getStatePropertySafe(state, FACING));
        }
        else {
            return IBlock.getVoxelShapeByDirection(0, 9, 0, 9, 12, 0.1, IBlock.getStatePropertySafe(state, FACING));
        }
    }

    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SchindlerMSeriesScreen2Even.BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
    }

    public static class BlockEntity extends BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_2_EVEN.get(), pos, state);
        }
    }
}
