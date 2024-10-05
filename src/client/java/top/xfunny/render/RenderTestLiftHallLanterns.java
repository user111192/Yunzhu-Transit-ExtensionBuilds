package top.xfunny.render;


import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.Init;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.RenderLifts;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.YteRouteMapGenerator;
import top.xfunny.block.OtisSeries1Button;
import top.xfunny.block.TestLiftHallLanterns;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;
import top.xfunny.resource.TextureList;
import top.xfunny.TextureCache;
import top.xfunny.util.GetLiftDetails;
import top.xfunny.util.ReverseRendering;
import java.util.Comparator;

public class RenderTestLiftHallLanterns extends BlockEntityRenderer<TestLiftHallLanterns.BlockEntity> implements DirectionHelper, IGui, IBlock {

	private static final int HOVER_COLOR = 0xFFADD8E6;
	private static final int PRESSED_COLOR = 0xFF0000FF;
	private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/lift_button.png");
	private static String CurrentFloorNumber = "";

	public RenderTestLiftHallLanterns(Argument dispatcher) {
		super(dispatcher);
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

		// 遍历每个轨道位置，进行后续处理
		blockEntity.forEachTrackPosition(trackPosition -> {
			// 手持连接器进行连线
			if (world.getBlockState(trackPosition).getBlock().data instanceof BlockLiftTrackFloor) {
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
				final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
				CurrentFloorNumber = RenderLifts.getLiftDetails(world, lift, trackPosition).right().left();
				ObjectArraySet<LiftDirection> isDestinationLevel = lift.hasInstruction(lift.getFloorIndex(top.xfunny.Init.blockPosToPosition(trackPosition)));

				top.xfunny.Init.LOGGER.info("currentFloorNumber:"+CurrentFloorNumber);
				top.xfunny.Init.LOGGER.info("LiftDetails:"+RenderLifts.getLiftDetails(world, lift, trackPosition));
				top.xfunny.Init.LOGGER.info("isDestinationLevel:"+isDestinationLevel);
//todo:目标外呼blockstates[]

				instructionDirections.forEach(liftDirection -> {
					switch (liftDirection) {
						case DOWN:
							buttonStates[2] = true;
							break;
						case UP:
							buttonStates[3] = true;
							break;
					}
				});
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
								-1.5F / 16,
								(buttonStates[1] ? 0.5F : 2.5F) / 16,
								3F / 16,
								3F / 16,
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
								-1.5F / 16,
								(buttonStates[0] ? 4.5F : 2.5F) / 16,
								3F / 16,
								3F / 16,
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
