package top.xfunny.render;


import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.RenderLifts;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.Init;
import top.xfunny.block.TestLiftHallLanterns;
import top.xfunny.block.base.LiftButtonsBase;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;
import top.xfunny.util.ClientGetLiftDetails;

import java.util.Comparator;
import java.util.Objects;

public class RenderTestLiftHallLanterns extends BlockEntityRenderer<TestLiftHallLanterns.BlockEntity> implements DirectionHelper, IGui, IBlock {

	private static final int PRESSED_COLOR = 0xFFCCFF33;
	private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/test_lift_lanterns_arrow.png");
	private final boolean isOdd;



	public RenderTestLiftHallLanterns(Argument dispatcher, Boolean isOdd) {
		super(dispatcher);
		this.isOdd = isOdd;
	}

	@Override
	public void render(TestLiftHallLanterns.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
		final World world = blockEntity.getWorld2();
		if (world == null) {
			return;
		}

		final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
		if (clientPlayerEntity == null) {
			return;
		}

		final BlockPos blockPos = blockEntity.getPos2();
		final BlockState blockState = world.getBlockState(blockPos);
		final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
		final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
		// 创建一个存储矩阵转换的实例，用于后续的渲染操作
		// 参数为方块的中心位置坐标 (x, y, z)
		final StoredMatrixTransformations storedMatrixTransformations1 = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);


		// 定义一个布尔数组，用于记录按钮的状态
		// 数组顺序：0向下按钮存在、1向上按钮存在、2向下按钮被按下、3向上按钮被按下
		final boolean[] buttonStates = {false, false, false, false};

		// 创建一个对象列表，用于存储排序后的位置和升降机的配对信息
		final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

		blockEntity.forEachButtonPosition(buttonPosition -> {
			// 手持连接器进行连线
			if (world.getBlockState(buttonPosition).getBlock().data instanceof LiftButtonsBase) {
				final Direction trackFacing = IBlock.getStatePropertySafe(world, buttonPosition, FACING);
				RenderLiftObjectLink.RenderButtonObjectLink(
						storedMatrixTransformations1,
						new Vector3d(facing.getOffsetX() / 2F, 0.5, facing.getOffsetZ() / 2F),
						new Vector3d(buttonPosition.getX() - blockPos.getX() + trackFacing.getOffsetX() / 2F, buttonPosition.getY() - blockPos.getY() + 0.5, buttonPosition.getZ() - blockPos.getZ() + trackFacing.getOffsetZ() / 2F),
						holdingLinker
				);
			}
		});
		// 遍历每个轨道位置，进行后续处理
		blockEntity.forEachTrackPosition(trackPosition -> {
			// 手持连接器进行连线
			if (world.getBlockState(trackPosition).getBlock().data instanceof BlockLiftTrackFloor ) {

				final Direction trackFacing = IBlock.getStatePropertySafe(world, trackPosition, FACING);
				RenderLiftObjectLink.RenderLiftObjectLink(
						storedMatrixTransformations1,
						new Vector3d(facing.getOffsetX() / 2F, 0.5, facing.getOffsetZ() / 2F),
						new Vector3d(trackPosition.getX() - blockPos.getX() + trackFacing.getOffsetX() / 2F, trackPosition.getY() - blockPos.getY() + 0.5, trackPosition.getZ() - blockPos.getZ() + trackFacing.getOffsetZ() / 2F),
						holdingLinker
				);
			}

			// Figure out whether the up and down buttons should be rendered
			TestLiftHallLanterns.hasButtonsClient(trackPosition, buttonStates, (floorIndex, lift) -> {
				// 确定是否渲染上下按钮，基于当前trackPosition和楼层信息
				// 该方法通过floorIndex和lift来决定是否添加trackPosition和lift到已排序的列表中
				// 同时，根据lift的方向（上或下），更新buttonStates数组以指示按钮的渲染状态
				// 这里使用lambda表达式来处理按钮状态的逻辑
				sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
				String CurrentFloorNumber = RenderLifts.getLiftDetails(world, lift, trackPosition).right().left();
				final ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, top.xfunny.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
				String floorNumber = liftDetails.right().left();
				LiftDirection buttonDirection = blockEntity.getButtonDirection();
				//top.xfunny.Init.LOGGER.info("Directions:"+lift.hasInstruction(floorIndex));
				//top.xfunny.Init.LOGGER.info("liftDetails:"+liftDetails);
				//top.xfunny.Init.LOGGER.info("currentFloorNumber:"+CurrentFloorNumber);
				//top.xfunny.Init.LOGGER.info("LiftDetails:"+RenderLifts.getLiftDetails(world, lift, trackPosition));
				//top.xfunny.Init.LOGGER.info("isDestinationLevel:"+isDestinationLevel);
				//top.xfunny.Init.LOGGER.info("buttondirection:"+LiftButtonsBase.getButtonDirection());
				//top.xfunny.Init.LOGGER.info("doorvalue:"+lift.getDoorValue());


				if(lift.getDoorValue()!=0) {
					switch (buttonDirection) {
						case DOWN:
							if (Objects.equals(CurrentFloorNumber, floorNumber)) {
								buttonStates[2] = true;
							}
							break;
						case UP:
							if (Objects.equals(CurrentFloorNumber, floorNumber)) {
								buttonStates[3] = true;
							}
							break;
						/*case NONE:
							if (Objects.equals(CurrentFloorNumber, floorNumber)) {
								buttonStates[2] = true;
								buttonStates[3] = true;
							}*/
					}
				}
			});
		});
		//top.xfunny.Init.LOGGER.info("sortedPositionsAndLifts:"+sortedPositionsAndLifts);

		// Sort list and only render the two closest lifts
		sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

		final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations1.copy();
		storedMatrixTransformations2.add(graphicsHolder -> {
			graphicsHolder.rotateYDegrees(-facing.asRotation());
			graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
		});

		// 根据按钮状态渲染按钮
		// 第一个按钮的渲染逻辑
		if (buttonStates[0]) {
			// 根据按钮的按下状态和鼠标位置选择不同的渲染层
			MainRenderer.scheduleRender(
					BUTTON_TEXTURE,
					false,
					QueuedRenderLayer.EXTERIOR,
					(graphicsHolder, offset) -> {
						// 应用存储的矩阵变换
						storedMatrixTransformations2.transform(graphicsHolder, offset);
						// 绘制按钮纹理，位置和颜色根据按钮状态和鼠标位置决定
						IDrawing.drawTexture(
								graphicsHolder,
								-2.0F / 16,
								(buttonStates[1] ? 3.5F : 6.5F) / 16,
								4F / 16,
								4F / 16,
								0,
								0,
								1,
								1,
								facing,
								buttonStates[2] ? PRESSED_COLOR :ARGB_GRAY,
								light
						);
						// 弹出当前图形状态
						graphicsHolder.pop();
					}
			);
		}
		// 第二个按钮的渲染逻辑
		if (buttonStates[1]) {
			// 根据按钮的按下状态和鼠标位置选择不同的渲染层
			MainRenderer.scheduleRender(
					BUTTON_TEXTURE,
					false,
					QueuedRenderLayer.EXTERIOR,
					(graphicsHolder, offset) -> {
						storedMatrixTransformations2.transform(graphicsHolder, offset);
						IDrawing.drawTexture(
								graphicsHolder,
								-2.0F / 16,
								(buttonStates[0] ? 8.5F : 6.5F) / 16,
								4F / 16,
								4F / 16,
								0,
								1,
								1,
								0,
								facing,
								buttonStates[3] ? PRESSED_COLOR : ARGB_GRAY,
								light
						);
						// 弹出当前图形状态
						graphicsHolder.pop();
					}
			);
		}
	}

}
