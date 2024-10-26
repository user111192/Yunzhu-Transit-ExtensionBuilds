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
import org.mtr.mod.InitClient;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.RenderLifts;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.Init;
import top.xfunny.block.SchindlerMSeriesScreen2;
import top.xfunny.block.TestLiftHallLanterns;
import top.xfunny.block.base.LiftButtonsBase;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;
import top.xfunny.resource.TextureList;
import top.xfunny.util.ClientGetLiftDetails;
import top.xfunny.util.ReverseRendering;

import java.util.Comparator;
import java.util.Objects;

import static org.mtr.mod.render.RenderLifts.renderLiftDisplay;

public class RenderSchindlerMSeriesScreen2 extends BlockEntityRenderer<SchindlerMSeriesScreen2.BlockEntity> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR = 0xFFCCFF33;
	private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_m_series_panel_arrow_1.png");
public RenderSchindlerMSeriesScreen2(Argument dispatcher) {
		super(dispatcher);
	}
    	@Override
	public void render(SchindlerMSeriesScreen2.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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

		blockEntity.forEachTrackPosition(trackPosition -> {
			if (world.getBlockState(trackPosition).getBlock().data instanceof BlockLiftTrackFloor) {
				final Direction trackFacing = IBlock.getStatePropertySafe(world, trackPosition, FACING);
				RenderLiftObjectLink.RenderLiftObjectLink(
						storedMatrixTransformations1,
						new Vector3d(facing.getOffsetX() / 2F, 0.5, facing.getOffsetZ() / 2F),
						new Vector3d(trackPosition.getX() - blockPos.getX() + trackFacing.getOffsetX() / 2F, trackPosition.getY() - blockPos.getY() + 0.5, trackPosition.getZ() - blockPos.getZ() + trackFacing.getOffsetZ() / 2F),
						holdingLinker
				);
			}


			TestLiftHallLanterns.hasButtonsClient(trackPosition, buttonStates, (floorIndex, lift) -> {
				sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
				String CurrentFloorNumber = RenderLifts.getLiftDetails(world, lift, trackPosition).right().left();
				final ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, top.xfunny.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
				String floorNumber = liftDetails.right().left();
				LiftDirection buttonDirection = blockEntity.getButtonDirection();


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
					}
				}else{
					if (blockEntity.getLanternMark()){
					}
				}
			});
		});
		sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

		final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations1.copy();
		storedMatrixTransformations2.add(graphicsHolder -> {
			graphicsHolder.rotateYDegrees(-facing.asRotation());
			graphicsHolder.translate(0, 0, 0.4968 - SMALL_OFFSET);
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
								3.5F / 16,
								3.4F / 16,
								1.8F / 16,
								2.2F / 16,
								0,
								0,
								1,
								1,
								facing,
								buttonStates[2] ? PRESSED_COLOR :ARGB_WHITE,
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
								-5.3F / 16,
								3.4F / 16,
								1.8F / 16,
								2.2F / 16,
								0,
								1,
								1,
								0,
								facing,
								buttonStates[3] ? PRESSED_COLOR : ARGB_WHITE,
								light
						);
						// 弹出当前图形状态
						graphicsHolder.pop();
					}
			);
		}

		if (!sortedPositionsAndLifts.isEmpty()) {
			// 确定要渲染的电梯数量，最多为2个
			final int count = 1;
			// 设置每个电梯显示的宽度，根据数量不同而变化
			final float width = 0.25F;

			// 创建当前矩阵变换的副本以供后续修改
			final StoredMatrixTransformations storedMatrixTransformations3 = storedMatrixTransformations2.copy();
			// 添加旋转和平移变换
			storedMatrixTransformations3.add(graphicsHolder -> {
				graphicsHolder.rotateZDegrees(180);
				graphicsHolder.translate(-width / 2, 0, 0);
			});

			// 渲染黑色背景
			MainRenderer.scheduleRender(new Identifier(org.mtr.mod.Init.MOD_ID, "textures/block/black.png"), false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
				storedMatrixTransformations3.transform(graphicsHolder, offset);
				IDrawing.drawTexture(graphicsHolder, 0, -0.342F, width, 0.12F, Direction.UP, light);
				graphicsHolder.pop();
			});

			// 遍历要渲染的每个电梯
			for (int i = 0; i < count; i++) {
				final double x = (i + 0.5) * width / count;
				final StoredMatrixTransformations storedMatrixTransformations4 = storedMatrixTransformations3.copy();
				storedMatrixTransformations4.add(graphicsHolder -> graphicsHolder.translate(x, -0.875, -SMALL_OFFSET));
				// 渲染当前电梯的显示
				renderLiftDisplay(storedMatrixTransformations4, world, sortedPositionsAndLifts.get(i).right(), width * 4  / count, 0.2F,0.2F,0.2F);

			}
		}
	}

		private void renderLiftDisplay(StoredMatrixTransformations storedMatrixTransformations, World world , Lift lift ,float width,float width1,float height1,float height) {
		// 获取电梯的详细信息，包括运行方向和楼层信息
		final ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, org.mtr.mod.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
		final LiftDirection liftDirection = liftDetails.left();
		final String floorNumber = liftDetails.right().left();
		final String floorDescription = liftDetails.right().right();

		// 判断楼层编号和描述是否为空
		final boolean noFloorNumber = floorNumber.isEmpty();
		final boolean noFloorDisplay = floorDescription.isEmpty();
		final float gameTick = InitClient.getGameTick(); // 获取当前游戏刻
		final boolean goingUp = liftDirection == LiftDirection.UP; // 判断电梯是否向上运行
		final float arrowSize = width / 6; // 设置箭头大小
		final float y = height; // 箭头的Y轴位置

		// 渲染楼层信息
		if (!noFloorNumber || !noFloorDisplay) {
			float offset1;
			final String text = String.format("%s%s", floorNumber, noFloorNumber? " " : "");
			int totalWidth = TextureList.instance.getTestLiftButtonsDisplay(text, 0xFFAA00).width;

			if (text.length() > 2) {
				float scrollSpeed = 0.008F;
				offset1 = (gameTick * scrollSpeed);
				if (offset1 > totalWidth - width1) {
					offset1 = offset1 - totalWidth;
				}
				float finalOffset = offset1;
				MainRenderer.scheduleRender(TextureList.instance.getTestLiftButtonsDisplay(text, 0xFFAA00).identifier, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
					storedMatrixTransformations.transform(graphicsHolder, offset);

					IDrawing.drawTexture(graphicsHolder, -width + 0.9F, y - 0.07F, width1, height1, finalOffset, 0, finalOffset+ (float) 170 /totalWidth, 1, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());//楼层数字尺寸设置
					graphicsHolder.pop();
				});
			} else {

				MainRenderer.scheduleRender(TextureList.instance.getTestLiftButtonsDisplay(text, 0xFFAA00).identifier, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
					storedMatrixTransformations.transform(graphicsHolder, offset);

					IDrawing.drawTexture(graphicsHolder, -width + 0.9F, y - 0.07F, width1, height1, 0, 0, 1, 1, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());//楼层数字尺寸设置
					graphicsHolder.pop();
				});
			}
		}
	}
}
