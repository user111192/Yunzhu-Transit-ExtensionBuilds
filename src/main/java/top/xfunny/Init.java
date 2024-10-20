package top.xfunny;

import org.mtr.core.data.LiftDirection;
import org.mtr.core.data.Position;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.MathHelper;
import org.mtr.mapping.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.xfunny.block.base.LiftHallLanternsBase;
import top.xfunny.util.GetLiftDetails;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static top.xfunny.block.base.LiftHallLanternsBase.callbackLift;

public final class Init {
	public static final String MOD_ID = "yte";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Registry REGISTRY = new Registry();


	public static void init()
	{

		Blocks.init();
		BlockEntityTypes.init();
		Items.init();
		REGISTRY.init();
	}

	public static Position blockPosToPosition(BlockPos blockPos) {
		return new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	public static BlockPos positionToBlockPos(Position position) {
		return new BlockPos((int)position.getX(), (int)position.getY(), (int)position.getZ());
	}

	public static BlockPos newBlockPos(double x, double y, double z) {
		return new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
	}

	public static void thread(BlockPos pos, LiftHallLanternsBase.BlockEntityBase blockEntityBase,String CurrentFloorNumber) {
		Thread t = new Thread(() -> {
			blockEntityBase.thread = false;
			blockEntityBase.setLanternMark(false);
			ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
			System.out.println("线程已开启");
			scheduler.scheduleAtFixedRate(
					new Runnable() {
						@Override
						public void run() {
							callbackLift(pos, (floor, lift) -> {
								ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = GetLiftDetails.getLiftDetails(Objects.requireNonNull(blockEntityBase.getWorld2()), lift, top.xfunny.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
								String floorNumber = liftDetails.right().left();
								Init.LOGGER.info("当前楼层:" + CurrentFloorNumber);
								Init.LOGGER.info("电梯楼层:" +floorNumber);
								Init.LOGGER.info("电梯开门程度:" + lift.getDoorValue());
								Init.LOGGER.info("队列:" + blockEntityBase.getQuene());
								if(blockEntityBase.getQuene().isEmpty()){
									Init.LOGGER.info("队列为空，关闭线程");
									blockEntityBase.thread = true;
									scheduler.shutdown();
								}
								if(lift.getDoorValue() == 1){
									blockEntityBase.setLanternMark(true);
								}
								Init.LOGGER.info("mark:" + blockEntityBase.getLanternMark());

								if(lift.getDoorValue() == 0 && blockEntityBase.getLanternMark() && Objects.equals(floorNumber, CurrentFloorNumber)){
									Init.LOGGER.info("电梯门已关闭，尝试清除一个元素");
									blockEntityBase.updateQueue();
									blockEntityBase.setLanternMark(false);
								}

							});
						}
					},
					0, 600, TimeUnit.MILLISECONDS
			);
		});
		t.start();
	}
}

