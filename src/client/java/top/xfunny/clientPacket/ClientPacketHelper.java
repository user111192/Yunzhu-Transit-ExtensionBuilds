package top.xfunny.clientPacket;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ScreenExtension;
import org.mtr.mod.block.*;
import org.mtr.mod.screen.*;
import top.xfunny.block.EmptyFloor;
import top.xfunny.screen.LiftEmptyFloorScreen;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Since packets are registered serverside, they will fail if any client classes are used (e.g. screens).
 */
public final class ClientPacketHelper {

	public static void openBlockEntityScreen(BlockPos blockPos) {
		getBlockEntity(blockPos, blockEntity -> {
			if (blockEntity.data instanceof BlockTrainAnnouncer.BlockEntity) {
				openScreen(new TrainAnnouncerScreen(blockPos, (BlockTrainAnnouncer.BlockEntity) blockEntity.data), screenExtension -> screenExtension instanceof TrainAnnouncerScreen);
			} else if (blockEntity.data instanceof BlockTrainScheduleSensor.BlockEntity) {
				openScreen(new TrainScheduleSensorScreen(blockPos, (BlockTrainScheduleSensor.BlockEntity) blockEntity.data), screenExtension -> screenExtension instanceof TrainScheduleSensorScreen);
			} else if (blockEntity.data instanceof BlockTrainSensorBase.BlockEntityBase) {
				openScreen(new TrainBasicSensorScreen(blockPos), screenExtension -> screenExtension instanceof TrainBasicSensorScreen);
			} else if (blockEntity.data instanceof BlockRailwaySign.BlockEntity || blockEntity.data instanceof BlockRouteSignBase.BlockEntityBase) {
				openScreen(new RailwaySignScreen(blockPos), screenExtension -> screenExtension instanceof RailwaySignScreen);
			} else if (blockEntity.data instanceof BlockLiftTrackFloor.BlockEntity) {
				openScreen(new LiftTrackFloorScreen(blockPos, (BlockLiftTrackFloor.BlockEntity) blockEntity.data), screenExtension -> screenExtension instanceof LiftTrackFloorScreen);
			} else if (blockEntity.data instanceof BlockSignalBase.BlockEntityBase) {
				openScreen(new SignalColorScreen(blockPos, (BlockSignalBase.BlockEntityBase) blockEntity.data), screenExtension -> screenExtension instanceof SignalColorScreen);
			} else if (blockEntity.data instanceof BlockEyeCandy.BlockEntity) {
				openScreen(new EyeCandyScreen(blockPos, (BlockEyeCandy.BlockEntity) blockEntity.data), screenExtension -> screenExtension instanceof EyeCandyScreen);
			} else if (blockEntity.data instanceof EmptyFloor.BlockEntity) {
				openScreen(new LiftEmptyFloorScreen(blockPos, (EmptyFloor.BlockEntity) blockEntity.data), screenExtension -> screenExtension instanceof LiftEmptyFloorScreen);
			}
		});
	}
	private static void openScreen(ScreenExtension screenExtension, Predicate<ScreenExtension> isInstance) {
		final MinecraftClient minecraftClient = MinecraftClient.getInstance();
		final Screen screen = minecraftClient.getCurrentScreenMapped();
		if (screen == null || screen.data instanceof ScreenExtension && !isInstance.test((ScreenExtension) screen.data)) {
			minecraftClient.openScreen(new Screen(screenExtension));
		}
	}

	private static void getBlockEntity(BlockPos blockPos, Consumer<BlockEntity> consumer) {
		final ClientWorld clientWorld = MinecraftClient.getInstance().getWorldMapped();
		if (clientWorld != null) {
			final BlockEntity blockEntity = clientWorld.getBlockEntity(blockPos);
			if (blockEntity != null) {
				consumer.accept(blockEntity);
			}
		}
	}
}
