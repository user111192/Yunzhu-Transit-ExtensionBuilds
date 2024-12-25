package top.xfunny;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockHelper;
import org.mtr.mod.block.BlockDirectionalDoubleBlockBase;
import org.mtr.mod.block.IBlock;

import javax.annotation.Nonnull;

public abstract class DoorBase extends BlockDirectionalDoubleBlockBase {

    public DoorBase() {
        super(BlockHelper.createBlockSettings(true).nonOpaque());
    }

    @Nonnull
    @Override
    public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(new ItemConvertible(asItem2().data));
    }

    @Nonnull
    @Override
    public VoxelShape getCameraCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }
}