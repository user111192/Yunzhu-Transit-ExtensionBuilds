package top.xfunny.item;

import org.mtr.mapping.holder.*;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.item.ItemBlockClickingBase;
import top.xfunny.ButtonRegistry;
import top.xfunny.Init;
import top.xfunny.LiftFloorRegistry;
import top.xfunny.block.base.LiftButtonsBase;
import top.xfunny.block.base.LiftDestinationDispatchTerminalBase;

import static top.xfunny.item.LinkerValidTypes.VALID_TYPES;

public class YteLiftButtonsLinker extends ItemBlockClickingBase {
    private final boolean isConnector;

    public YteLiftButtonsLinker(boolean isConnector, ItemSettings itemSettings) {
        super(itemSettings.maxCount(1));
        this.isConnector = isConnector;
    }

    private static void connect(World world, BlockPos blockPos1, BlockPos blockPos2, boolean isAdd) {
        final BlockEntity blockEntity1 = world.getBlockEntity(blockPos1);
        final BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);

        // 合并日志输出，减少信息泄露风险
        if (blockEntity1 != null && blockEntity2 != null) {
            Init.LOGGER.info("正在尝试连接 {} 和 {}", blockPos1, blockPos2);

            // 简化类型检查
            if (blockEntity2.data instanceof BlockLiftTrackFloor.BlockEntity) {
                if (blockEntity1.data instanceof LiftFloorRegistry) {
                    ((LiftFloorRegistry) blockEntity1.data).registerFloor(blockPos1, world, blockPos2, isAdd);
                    Init.LOGGER.info("已成功连接 {} 和 {}", blockPos1, blockPos2);
                } else {
                    Init.LOGGER.info("未能连接 {} 和 {}", blockPos1, blockPos2);
                }
            } else if (blockEntity2.data instanceof LiftButtonsBase.BlockEntityBase || blockEntity2.data instanceof LiftDestinationDispatchTerminalBase.BlockEntityBase) {
                if (blockEntity1.data instanceof ButtonRegistry) {
                    ((ButtonRegistry) blockEntity2.data).registerButton(world, blockPos1, isAdd);
                    ((ButtonRegistry) blockEntity1.data).registerButton(world, blockPos2, isAdd);
                    Init.LOGGER.info("已成功绑定按钮 {} 和 {}", blockPos1, blockPos2);
                } else {
                    Init.LOGGER.info("未能绑定按钮 {} 和 {}", blockPos1, blockPos2);
                }

            }
        } else {
            Init.LOGGER.warn("BlockEntity 为空，无法连接 {} 和 {}", blockPos1, blockPos2);
        }
    }

    @Override
    protected void onStartClick(ItemUsageContext context, CompoundTag compoundTag) {
        Init.LOGGER.info("Clicked lift buttons linker");
    }

    @Override
    protected void onEndClick(ItemUsageContext context, BlockPos posEnd, CompoundTag compoundTag) {
        final World world = context.getWorld();
        final BlockPos posStart = context.getBlockPos();
        connect(world, posStart, posEnd, isConnector);
        Init.LOGGER.info("c1");
        connect(world, posEnd, posStart, isConnector);
        Init.LOGGER.info("c2");
        Init.LOGGER.info("Connected lift buttons at " + posStart.getY() + " and " + posEnd.getY());
    }

    private boolean isValidType(Object data) {
        return VALID_TYPES.contains(data.getClass());
    }

    @Override
    protected boolean clickCondition(ItemUsageContext context) {
        final Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
        return isValidType(block.data);
    }


}
