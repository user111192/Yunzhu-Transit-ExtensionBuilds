package top.xfunny.block.base;

import org.jetbrains.annotations.NotNull;
import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.*;
import org.mtr.mod.block.BlockLiftPanelBase;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.render.RenderLifts;
import top.xfunny.ButtonRegistry;
import top.xfunny.Init;
import top.xfunny.Items;
import top.xfunny.LiftFloorRegistry;
import top.xfunny.util.GetLiftDetails;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.mtr.core.data.LiftDirection.NONE;


public abstract class LiftHallLanternsBase extends BlockExtension implements DirectionHelper, BlockWithEntity, IBlock{

	private final boolean isOdd;

	public LiftHallLanternsBase(Boolean isOdd) {
		super(BlockHelper.createBlockSettings(true));
		Init.LOGGER.info("LiftHallLanternsBase init");
		this.isOdd = isOdd;
	}

	/**
	 * 检查电梯轨道位置是否存在上下按钮的客户端方法
	 * 本方法主要用于确定在给定的电梯轨道位置是否有上下按钮，以及通知相关的回调函数
	 *
	 * @param trackPosition the position of the lift floor track 电梯轨道的位置
	 * @param buttonStates  an array with at least 2 elements: has down button, has up button 一个至少包含两个元素的数组，表示是否有向下和向上的按钮
	 * @param callback      a callback for the lift and floor index, only run if the lift floor track exists in the lift
	 *                      关于电梯和楼层索引的回调函数，只有当电梯地板轨道存在于电梯中时才运行
	 */
	public static void hasButtonsClient(BlockPos trackPosition, boolean[] buttonStates, FloorLiftCallback callback) {
		// 获取实例中的所有电梯数据
		MinecraftClientData.getInstance().lifts.forEach(lift -> {
			// 获取电梯轨道位置对应的楼层索引
			final int floorIndex = lift.getFloorIndex(Init.blockPosToPosition(trackPosition));
			// 如果楼层索引大于0，则表示存在向下按钮
			if (floorIndex > 0) {
				buttonStates[0] = true;
			}
			// 如果楼层索引在有效范围内（不是顶层也不是底层），则表示存在向上按钮
			if (floorIndex >= 0 && floorIndex < lift.getFloorCount() - 1) {
				buttonStates[1] = true;
			}
			// 如果楼层索引非负，表示电梯中存在该楼层，执行回调函数
			if (floorIndex >= 0) {
				callback.accept(floorIndex, lift);
			}
		});
	}

	public static void callbackLift(BlockPos trackPosition, FloorLiftCallback callback){
		MinecraftClientData.getInstance().lifts.forEach(lift -> {
			final int floorIndex = lift.getFloorIndex(Init.blockPosToPosition(trackPosition));
			if (floorIndex >= 0) {
				callback.accept(floorIndex, lift);
			}
		});
	}


	@Nonnull
	@Override
	public ActionResult onUse2(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockHitResult hit) {
		final ActionResult result = IBlock.checkHoldingBrush(world, player, () -> {
			final BlockEntity entity = world.getBlockEntity(pos);
			Init.LOGGER.info(entity.toString());
			Init.LOGGER.info( "刷子选中的方向队列："+ ((BlockEntityBase) entity.data).getQuene()+"坐标："+ pos.toShortString());
			((BlockEntityBase) entity.data).clearQueue();
			player.sendMessage(Text.of("已清除方向队列"), true);
			//player.sendMessage((unlocked ? TranslationProvider.GUI_MTR_LIFT_BUTTONS_UNLOCKED : TranslationProvider.GUI_MTR_LIFT_BUTTONS_LOCKED).getText(), true);
		});

		if (result == ActionResult.SUCCESS) {
			return ActionResult.SUCCESS;
		}else{
			return player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get()) || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER.get()) ? ActionResult.PASS : ActionResult.FAIL;
		}
	}

	@Nonnull
	@Override
	public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos){
		if(!isOdd){
			if (IBlock.getSideDirection(state) == direction && !neighborState.isOf(new Block(this))) {
				return Blocks.getAirMapped().getDefaultState();
			} else {
				return state;
			}
		}else{
			return state;
		}
	}

	@Override
	public BlockState getPlacementState2(ItemPlacementContext ctx) {
		final Direction facing = ctx.getPlayerFacing();
		if(!isOdd){
			return IBlock.isReplaceable(ctx, facing.rotateYClockwise(), 2) ? getDefaultState2().with(new Property<>(FACING.data), facing.data).with(new Property<>(SIDE.data), IBlock.EnumSide.LEFT) : null;
		}else{
			return getDefaultState2().with(new Property<>(FACING.data), facing.data);
		}
	}

	@Override
	public void onPlaced2(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack){
		if(!world.isClient()){
			final Direction facing = IBlock.getStatePropertySafe(state, FACING);
			if(!isOdd){
				world.setBlockState(pos.offset(facing.rotateYClockwise()), getDefaultState2().with(new Property<>(FACING.data), facing.data).with(new Property<>(SIDE.data), IBlock.EnumSide.RIGHT), 3);
			}
			world.updateNeighbors(pos, Blocks.getAirMapped());
			state.updateNeighbors(new WorldAccess(world.data), pos, 3);
		}
	}

	@Override
	public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player){
		if(!isOdd){
			if (IBlock.getStatePropertySafe(state, SIDE) == IBlock.EnumSide.RIGHT) {
				IBlock.onBreakCreative(world, player, pos.offset(IBlock.getSideDirection(state)));
			}
		}
		super.onBreak2(world, pos, state, player);
	}



	public static class BlockEntityBase extends BlockEntityExtension implements LiftFloorRegistry, ButtonRegistry {
		// 用于在CompoundTag中标识地板位置数组的键
		private static final String KEY_TRACK_FLOOR_POS = "track_floor_pos";
		private static final String KEY_BUTTON_FLOOR_POS = "button_floor_pos";
		// 存储需要追踪的位置的集合
		private final ObjectOpenHashSet<BlockPos> trackPositions = new ObjectOpenHashSet<>();
		private final ObjectOpenHashSet<BlockPos> buttonPositions = new ObjectOpenHashSet<>();
		public final LinkedList<LiftDirection> directionQueue = new LinkedList<>();
		private boolean lanternMark = false;
		public boolean thread = true;
		public BlockPos selfPos;
		private LiftDirection buttonDirection = NONE;
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

		public BlockEntityBase(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
			super(type, blockPos, blockState);
		}

		public void setLanternMark(boolean lanternMark){
			this.lanternMark = lanternMark;
		}

		public boolean getLanternMark(){
			return lanternMark;
		}

		public void setLiftDirection(LiftDirection direction) {

			Init.LOGGER.info("（到站灯）检测到按下了："+ direction);
			Init.LOGGER.info("添加前的方向队列："+ directionQueue);
			Init.LOGGER.info("添加前的方向队列(get方法)："+ getQuene());
			Init.LOGGER.info("---------------------------------------------------------------");
			forEachTrackPosition(pos -> {
				callbackLift(pos, (floor, lift) -> {
					Init.LOGGER.info("callback成功：" + lift.hasInstruction(floor)+ "楼层："+ floor+ "轨道坐标："+ pos.toShortString());
					Init.LOGGER.info("overlappingFloors:" + lift.overlappingFloors(lift)+":"+lift.getCurrentFloor());
					Init.LOGGER.info("楼层数:" + lift.getFloorCount());
					String CurrentFloorNumber = RenderLifts.getLiftDetails(Objects.requireNonNull(this.getWorld2()), lift, pos).right().left();
					ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = GetLiftDetails.getLiftDetails(this.getWorld2(), lift, top.xfunny.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
					String floorNumber = liftDetails.right().left();
					ObjectArraySet<LiftDirection> liftDirections = lift.hasInstruction(floor);
					Queue<LiftDirection> directionQueue = this.getQuene();
					float LiftDoorValue = lift.getDoorValue();


					if(LiftDoorValue==0 && Objects.equals(CurrentFloorNumber, floorNumber)){//轨道当前楼层与按钮楼层一致，且电梯没有开门
						Init.LOGGER.info("满足条件1");
						if(liftDirections.isEmpty()){//指令列表为空
							Init.LOGGER.info("方向添加成功！--电梯无指令");
							directionQueue.add(direction);
							updateLiftDirection();
						}else{//指令列表不为空：电梯经过本层但不停
							try {
								throw new Exception("发生错误，已清空队列");
							} catch (Exception e) {
								Init.LOGGER.error("发生错误，已清空队列", e);
								directionQueue.clear();
							}
						}
					}else{
						Init.LOGGER.info("不满足条件1");
						executor.schedule(() -> {
							callbackLift(pos, (floor1, lift1) -> {
								if(!containsValueExceptFirst(directionQueue, direction)){
									ObjectArraySet<LiftDirection> liftDirections1 = lift1.hasInstruction(floor1);
									Init.LOGGER.info("liftDirections:"+liftDirections1);
									Iterator<LiftDirection> iterator = liftDirections1.iterator();
									LiftDirection firstDirection = iterator.next();
									directionQueue.add(firstDirection);
									updateLiftDirection();
								}else{
									Init.LOGGER.info("方向已存在");
								}
							});
						}, 500, TimeUnit.MILLISECONDS);
					}

					executor.schedule(() -> {
						if(thread){
							Init.thread(getTrackPosition(), this, CurrentFloorNumber);
						}else{
							Init.LOGGER.info("线程已经开启");
						}}, 501, TimeUnit.MILLISECONDS);
				});
			});
		}

		public void updateQueue() {
			forEachTrackPosition(pos -> {
				callbackLift(pos, (floor, lift) -> {
					String CurrentFloorNumber = RenderLifts.getLiftDetails(Objects.requireNonNull(this.getWorld2()), lift, pos).right().left();
					ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = GetLiftDetails.getLiftDetails(this.getWorld2(), lift, top.xfunny.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
					String floorNumber = liftDetails.right().left();
					if(Objects.equals(floorNumber, CurrentFloorNumber)){
						if (!directionQueue.isEmpty()) {
							if(directionQueue.contains(lift.getDirection())){
								directionQueue.remove(lift.getDirection());
							}else{
								Init.LOGGER.info(String.valueOf(lift.getDirection())+":"+directionQueue);
								directionQueue.poll();
								Init.LOGGER.info("没有找到对应的方向");
							}
							updateLiftDirection();
							Init.LOGGER.info("LiftHallLanternBase,updateQueue()时不为空"+ directionQueue);
						} else {
							Init.LOGGER.info("LiftHallLanternBase,updateQueue()时为空");
						}
					}else{
						Init.LOGGER.info("电梯未停靠本层");
					}
				});
			});
		}

		public void clearQueue() {
			directionQueue.clear();
			buttonDirection = NONE;
		}


		public void updateLiftDirection() {
			if (!directionQueue.isEmpty()) {
				forEachTrackPosition(pos -> {
					callbackLift(pos, (floor, lift) -> {
						LiftDirection tempLiftDirection = lift.getDirection();
						buttonDirection = directionQueue.contains(tempLiftDirection)?  tempLiftDirection : NONE;
					});
				});
			}
		}

		public  LiftDirection getButtonDirection() {
			//Init.LOGGER.info("LiftHallLanternBase,getButtonDirection():"+buttonDirection);
			return buttonDirection;
		}

		public Queue<LiftDirection> getQuene() {
			return directionQueue;
		}

		public static boolean containsValueExceptFirst(Queue<LiftDirection> directionQueue, LiftDirection value) {
			if (directionQueue == null || directionQueue.isEmpty()) {
				return false;
			}

			Iterator<LiftDirection> iterator = directionQueue.iterator();
			if (!iterator.hasNext()) {
				return false;
			}


			iterator.next();

			while (iterator.hasNext()) {
				if (iterator.next().equals(value)) {
					return true;
				}
			}

			return false;
		}

		/**
		 * 从CompoundTag中读取数据，用于加载位置信息到trackPositions集合中
		 *
		 * @param compoundTag 包含方块实体数据的CompoundTag
		 */
		@Override
		public void readCompoundTag(CompoundTag compoundTag) {
			Init.LOGGER.info("LiftHallLanternBase,readCompoundTag()");
			// 清空当前位置集合，准备加载新的数据
			trackPositions.clear();
			buttonPositions.clear();

			// 从CompoundTag中读取名为KEY_TRACK_FLOOR_POS的长整型数组
			// 每个长整型代表一个BlockPos位置，将其转换并添加到trackPositions集合中
			for (final long position : compoundTag.getLongArray(KEY_TRACK_FLOOR_POS)) {
				trackPositions.add(BlockPos.fromLong(position));
			}
			for (final long position : compoundTag.getLongArray(KEY_BUTTON_FLOOR_POS)) {
				buttonPositions.add(BlockPos.fromLong(position));
			}
		}

		public BlockPos getTrackPosition(){
			BlockPos trackPosition = (BlockPos) trackPositions.toArray()[0];
			return trackPosition;
		}

		/**
		 * 重写writeCompoundTag方法，将当前类中的数据保存到CompoundTag中
		 * 主要用于序列化TrackFloor对象的数据，以便存储或传输
		 *
		 * @param compoundTag 一个CompoundTag对象，用于存储TrackFloor的数据
		 */
		@Override
		public void writeCompoundTag(CompoundTag compoundTag) {
			// 创建一个临时的List，用于存储trackPositions的长整型表示
			final List<Long> trackPositionsList = new ArrayList<>();
			final List<Long> buttonPositionsList = new ArrayList<>();

			// 遍历trackPositions集合，将每个位置转换为长整型并添加到trackPositionsList中
			// 这里的转换是为了以长整型数组的形式存储这些位置信息
			trackPositions.forEach(position -> trackPositionsList.add(position.asLong()));
			buttonPositions.forEach(position -> buttonPositionsList.add(position.asLong()));

			// 将收集到的trackPositions长整型列表以数组的形式存储到compoundTag中
			// 使用的键是KEY_TRACK_FLOOR_POS，值是trackPositionsList数组
			compoundTag.putLongArray(KEY_TRACK_FLOOR_POS, trackPositionsList);
			compoundTag.putLongArray(KEY_BUTTON_FLOOR_POS, buttonPositionsList);
		}

		/**
		 * 注册或取消注册一个楼层位置
		 * 该方法用于控制哪些楼层位置被跟踪以及在更改时标记数据为脏
		 *
		 * @param pos   需要注册或取消注册的楼层位置，使用BlockPos表示
		 * @param isAdd 指示是注册还是取消注册的操作类型；true表示注册，false表示取消注册
		 */
		public void registerFloor(World world, BlockPos pos, boolean isAdd) {
			Init.LOGGER.info("正在操作");
			if (IBlock.getStatePropertySafe(world, getPos2(), SIDE) == EnumSide.RIGHT) {
				final BlockEntity blockEntity = world.getBlockEntity(getPos2().offset(IBlock.getStatePropertySafe(world, getPos2(), FACING).rotateYCounterclockwise()));
				if (blockEntity != null && blockEntity.data instanceof LiftHallLanternsBase.BlockEntityBase) {
					((LiftHallLanternsBase.BlockEntityBase) blockEntity.data).registerFloor(world, pos, isAdd);
				}
			} else {
				if (isAdd) {
					if (trackPositions.isEmpty()){
						// 如果是添加操作，则将位置添加到跟踪列表中
						trackPositions.add(pos);
						Init.LOGGER.info("已添加");
					}else {
						Init.LOGGER.info("只能连接一个楼层轨道");
					}

				} else {
					// 如果是非添加操作，则从跟踪列表中移除该位置
					trackPositions.remove(pos);
					Init.LOGGER.info("已移除");
				}
				// 更新数据状态，标记数据为“脏”，表示需要保存或同步
				markDirty2();
			}
		}

		public void registerButton(World world, BlockPos blockPos, boolean isAdd) {
			Init.LOGGER.info("正在操作");
			if (IBlock.getStatePropertySafe(world, getPos2(), SIDE) == EnumSide.RIGHT) {
				final BlockEntity blockEntity = world.getBlockEntity(getPos2().offset(IBlock.getStatePropertySafe(world, getPos2(), FACING).rotateYCounterclockwise()));
				if (blockEntity != null && blockEntity.data instanceof LiftHallLanternsBase.BlockEntityBase) {
					((LiftHallLanternsBase.BlockEntityBase) blockEntity.data).registerButton(world, blockPos, isAdd);
				}
			}else{
				if (isAdd) {
				if(buttonPositions.isEmpty()){
					// 如果是添加操作，则将位置添加到跟踪列表中
					buttonPositions.add(blockPos);
					Init.LOGGER.info("按钮已添加");
				}else {
					Init.LOGGER.info("只能连接一个按钮");
				}


			} else {
				buttonPositions.remove(blockPos);
				Init.LOGGER.info("按钮已移除");
				Init.LOGGER.info("blockpos:"+blockPos);
				Init.LOGGER.info("buttonPositions"+buttonPositions);
			}
			markDirty2();
			}

		}
		/**
		 * 对每个轨道位置执行给定的操作
		 * <p>
		 * 该方法通过遍历所有轨道位置并对其执行给定的操作来提供一种回调机制，这有助于在轨道位置集合上执行统一的操作，
		 * 而无需手动实现遍历逻辑
		 *
		 * @param consumer 执行的操作，接受 {@link BlockPos} 作为参数每个轨道位置都将传递给这个操作
		 */
		public void forEachTrackPosition(Consumer<BlockPos> consumer) {
			trackPositions.forEach(consumer);
		}
		public void forEachButtonPosition(Consumer<BlockPos> consumer) {
			buttonPositions.forEach(consumer);
		}
	}

	@FunctionalInterface
	public interface FloorLiftCallback {
		void accept(int floor, Lift lift);
	}
}

