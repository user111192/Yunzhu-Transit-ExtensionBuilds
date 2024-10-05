package top.xfunny.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.BlockEntityTypes;
import top.xfunny.block.base.LiftPanelBase;

import javax.annotation.Nonnull;
import java.util.List;

public class SchindlerMSeriesScreen1 extends LiftPanelBase {
    public SchindlerMSeriesScreen1() {
        super();
    }

    @Nonnull
	@Override
	public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return IBlock.getVoxelShapeByDirection(4, 0, 0, 12, 8, 1, IBlock.getStatePropertySafe(state, FACING));
	}

    @Nonnull
	@Override
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new SchindlerMSeriesScreen1.BlockEntity(blockPos, blockState);
	}

	@Override
	public void addBlockProperties(List<HolderBase<?>> properties) {
		// 添加块的方向属性
		properties.add(FACING);
	}

	public static class BlockEntity extends BlockEntityBase {

		public BlockEntity(BlockPos pos, BlockState state) {
			super(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_1.get(), pos, state);
		}
	}

}
