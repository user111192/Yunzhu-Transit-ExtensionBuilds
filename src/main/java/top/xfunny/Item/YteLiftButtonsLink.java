package top.xfunny.Item;

import org.mtr.mapping.holder.*;

import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.item.ItemBlockClickingBase;
import top.xfunny.Block.TestLiftButtons;
import top.xfunny.Init;

public class YteLiftButtonsLink extends ItemBlockClickingBase {
    private final boolean isConnector;

	public YteLiftButtonsLink(boolean isConnector, ItemSettings itemSettings) {
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
		final BlockPos posStart = context.getBlockPos();
		connect(world, posStart, posEnd, isConnector);
		connect(world, posEnd, posStart, isConnector);
		Init.LOGGER.info("Connected lift buttons at " + posStart + " and " + posEnd);
	}

	@Override
	protected boolean clickCondition(ItemUsageContext context) {
		final Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
		return block.data instanceof BlockLiftTrackFloor || block.data instanceof TestLiftButtons;
	}

	private static void connect(World world, BlockPos blockPos1, BlockPos blockPos2, boolean isAdd) {
		final BlockEntity blockEntity1 = world.getBlockEntity(blockPos1);
		final BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);
		Init.LOGGER.info(String.valueOf(blockEntity1.data.getClass()));
		Init.LOGGER.info("connect运行了");
		if (blockEntity1 != null && blockEntity2 != null && blockEntity2.data instanceof BlockLiftTrackFloor.BlockEntity) {
			Init.LOGGER.info("if运行了");
			if (blockEntity1.data instanceof TestLiftButtons.BlockEntity) {
				((TestLiftButtons.BlockEntity) blockEntity1.data).registerFloor(blockPos2, isAdd);
				Init.LOGGER.info("已连接");
			}else{
				Init.LOGGER.info("未连接");
			}


		}
	}



}
