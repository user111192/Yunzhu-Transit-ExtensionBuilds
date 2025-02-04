package top.xfunny.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.BlockEntityTypes;
import top.xfunny.Init;
import top.xfunny.block.base.LiftDestinationDispatchTerminalBase;
import top.xfunny.keymapping.TestLiftDestinationDispatchTerminalKeyMapping;

import javax.annotation.Nonnull;
import java.util.List;

public class TestLiftDestinationDispatchTerminal extends LiftDestinationDispatchTerminalBase {
    public String screenId = "test_lift_destination_dispatch_terminal_key_mapping_home";

    public TestLiftDestinationDispatchTerminal() {
        super(true);
    }


    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 11, 1, IBlock.getStatePropertySafe(state, FACING));
    }

    /**
     * 创建方块实体扩展
     * 此方法用于实例化与电梯按钮相关的方块实体
     *
     * @param blockPos   方块的位置
     * @param blockState 方块的状态
     * @return 返回一个新的 {@code BlockEntityExtension} 实例，代表电梯按钮的方块实体
     */
    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TestLiftDestinationDispatchTerminal.BlockEntity(blockPos, blockState);
    }

    /**
     * 添加块属性
     * 此方法用于向块的属性列表中添加方向和解锁状态属性
     *
     * @param properties 块的属性列表，包含所有与块相关的属性
     */
    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        // 添加块的方向属性
        properties.add(FACING);
        // 添加块的解锁状态属性
        properties.add(UNLOCKED);

    }

    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final double hitY = MathHelper.fractionalPart(hit.getPos().getYMapped());
        final double hitX = MathHelper.fractionalPart(hit.getPos().getXMapped());

        TestLiftDestinationDispatchTerminalKeyMapping mapping = new TestLiftDestinationDispatchTerminalKeyMapping();
        String testOutput = mapping.mapping(screenId, hitX, hitY);
        Init.LOGGER.info("hitx:"+hitX+",hity:"+hitY+",您点击了" + testOutput);

        return ActionResult.SUCCESS;
    }

    /**
     * 表示一个可追踪位置的方块实体，扩展自BlockEntityExtension
     * 主要功能是通过CompoundTag来读取和写入特定位置集合
     */
    public static class BlockEntity extends LiftDestinationDispatchTerminalBase.BlockEntityBase {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.TEST_LIFT_DESTINATION_DISPATCH_TERMINAL.get(), pos, state);
        }
    }
}
