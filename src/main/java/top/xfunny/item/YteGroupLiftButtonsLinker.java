package top.xfunny.item;

import org.mtr.mapping.holder.*;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.item.ItemBlockClickingBase;
import top.xfunny.Init;
import top.xfunny.LiftFloorRegistry;
import top.xfunny.block.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class YteGroupLiftButtonsLinker extends ItemBlockClickingBase {
    private final boolean isConnector;
	public YteGroupLiftButtonsLinker(boolean isConnector, ItemSettings itemSettings) {
		super(itemSettings.maxCount(1));
		this.isConnector = isConnector;
	}
    @Override
	protected void onStartClick(ItemUsageContext context, CompoundTag compoundTag) {
		Init.LOGGER.info("Clicked lift buttons linker");
	}


	@Override
	protected void onEndClick(ItemUsageContext context, BlockPos posEnd, CompoundTag compoundTag) {
		final World world = context.getWorld();

		BlockPos posStart = context.getBlockPos();
		Vector3i vector3i = new Vector3i(posStart.getX(), 320, posStart.getZ());
		Init.LOGGER.info("c0");
        for (posStart = context.getBlockPos();posStart.compareTo(vector3i)!=0;posStart.up(1)) {
            connect(world, posStart, posEnd, isConnector);
            Init.LOGGER.info("c1");
            connect(world, posEnd, posStart, isConnector);
            Init.LOGGER.info("c2");
            Init.LOGGER.info("Connected lift buttons at " + posStart.toShortString()+ " and " + posEnd.toShortString());
            posStart = posStart.add(0,1,0);
            posEnd = posEnd.add(0,1,0);
        }
    }

	private static final Set<Class<?>> VALID_TYPES = new HashSet<>(Arrays.asList(
			BlockLiftTrackFloor.class,
			TestLiftButtons.class,
			TestLiftButtonsWithoutScreen.class,
			OtisSeries1Button.class,
			TestLiftHallLanterns.class,
			TestLiftPanel.class

	));

	private boolean isValidType(Object data) {
		return VALID_TYPES.contains(data.getClass());
	}

	@Override
	protected boolean clickCondition(ItemUsageContext context) {
		final Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
		return isValidType(block.data);
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
                ((LiftFloorRegistry) blockEntity1.data).registerFloor(blockPos2, isAdd);
                Init.LOGGER.info("已成功连接 {} 和 {}", blockPos1, blockPos2);
            } else {
                Init.LOGGER.info("未能连接 {} 和 {}", blockPos1, blockPos2);
            }
        } else {
            Init.LOGGER.info("未能连接 {} 和 {}", blockPos1, blockPos2);
        }
    } else {
        Init.LOGGER.warn("BlockEntity 为空，无法连接 {} 和 {}", blockPos1, blockPos2);
    }
}


}
