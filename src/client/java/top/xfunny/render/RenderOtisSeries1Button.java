package top.xfunny.render;


import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.Init;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.block.OtisSeries1Button;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;
import java.util.Comparator;

public class RenderOtisSeries1Button extends BlockEntityRenderer<OtisSeries1Button.BlockEntity> implements DirectionHelper, IGui, IBlock {

	private static final int HOVER_COLOR = 0xFFFFFFFF;
	private static final int PRESSED_COLOR = 0xFFFFCB3B;
	private static final Identifier BUTTON_TEXTURE = new Identifier(top.xfunny.Init.MOD_ID, "textures/block/otis_s1_button.png");
	private static final Identifier ARROW_TEXTURE = new Identifier(top.xfunny.Init.MOD_ID, "textures/block/otis_s1_arrow.png");


	public RenderOtisSeries1Button(Argument dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(OtisSeries1Button.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay){
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
		// 数组顺序：向下按钮存在、向上按钮存在、向下按钮被按下、向上按钮被按下
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
						new Vector3d(facing.getOffsetX() / 2F, 0.2, facing.getOffsetZ() / 2F),
						new Vector3d(trackPosition.getX() - blockPos.getX() + trackFacing.getOffsetX() / 2F, trackPosition.getY() - blockPos.getY() + 0.5, trackPosition.getZ() - blockPos.getZ() + trackFacing.getOffsetZ() / 2F),
						holdingLinker
				);
			}

			// Figure out whether the up and down buttons should be rendered
			OtisSeries1Button.hasButtonsClient(trackPosition, buttonStates, (floorIndex, lift) -> {
				// 确定是否渲染上下按钮，基于当前trackPosition和楼层信息
				// 该方法通过floorIndex和lift来决定是否添加trackPosition和lift到已排序的列表中
				// 同时，根据lift的方向（上或下），更新buttonStates数组以指示按钮的渲染状态
				// 这里使用lambda表达式来处理按钮状态的逻辑
				sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
				final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
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

		// Sort list and only render the two closest lifts
		sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

		// Check whether the player is looking at the top or bottom button
		final HitResult hitResult = MinecraftClient.getInstance().getCrosshairTargetMapped();
		final boolean lookingAtTopHalf;
		final boolean lookingAtBottomHalf;
		if (hitResult == null || !IBlock.getStatePropertySafe(blockState, OtisSeries1Button.UNLOCKED)) {
			lookingAtTopHalf = false;
			lookingAtBottomHalf = false;
		} else {
			final Vector3d hitLocation = hitResult.getPos();
			final double hitY = MathHelper.fractionalPart(hitLocation.getYMapped());
			final boolean inBlock = hitY < 0.5 && Init.newBlockPos(hitLocation.getXMapped(), hitLocation.getYMapped(), hitLocation.getZMapped()).equals(blockPos);
			lookingAtTopHalf = inBlock && (!buttonStates[0] || hitY > 0.25);
			lookingAtBottomHalf = inBlock && (!buttonStates[1] || hitY < 0.25);
		}

		final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations1.copy();
		storedMatrixTransformations2.add(graphicsHolder -> {
			graphicsHolder.rotateYDegrees(-facing.asRotation());
			graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
		});

		final StoredMatrixTransformations storedMatrixTransformations3 = storedMatrixTransformations2.copy();
			// 添加旋转和平移变换
			storedMatrixTransformations3.add(graphicsHolder -> {
				graphicsHolder.rotateZDegrees(180);
				graphicsHolder.translate(-0.5F / 2, 0, 0.001);
			});

		// 根据按钮状态渲染按钮
		// 第一个按钮的渲染逻辑
		if (buttonStates[0]) {
			MainRenderer.scheduleRender(new Identifier(Init.MOD_ID, "textures/block/black.png"), false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
				storedMatrixTransformations3.transform(graphicsHolder, offset);
				IDrawing.drawTexture(graphicsHolder, 0.15F, (buttonStates[1] ? -5.4F : -4.8F) / 16, 0.2F, 0.1F, Direction.UP, light);
				graphicsHolder.pop();
			});
			// 根据按钮的按下状态和鼠标位置选择不同的渲染层
			MainRenderer.scheduleRender(
					BUTTON_TEXTURE,
					false,
					buttonStates[2] || lookingAtBottomHalf ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.INTERIOR,
					(graphicsHolder, offset) -> {
						// 应用存储的矩阵变换
						storedMatrixTransformations2.transform(graphicsHolder, offset);
						// 绘制按钮纹理，位置和颜色根据按钮状态和鼠标位置决定
						IDrawing.drawTexture(
								graphicsHolder,
								-1.25F / 16,
								(buttonStates[1] ? 2.9F : 3.5F) / 16,
								1F / 16,
								1F / 16,
								0,
								0,
								1,
								1,
								facing,
								buttonStates[2] ? PRESSED_COLOR : lookingAtBottomHalf ? HOVER_COLOR : 0xFFFFFFFF,
								light
						);
						// 弹出当前图形状态
						graphicsHolder.pop();
					}

			);
			MainRenderer.scheduleRender(
					ARROW_TEXTURE,
					false,
					QueuedRenderLayer.EXTERIOR,
					(graphicsHolder, offset) -> {
						// 应用存储的矩阵变换
						storedMatrixTransformations2.transform(graphicsHolder, offset);
						// 绘制按钮纹理，位置和颜色根据按钮状态和鼠标位置决定
						IDrawing.drawTexture(
								graphicsHolder,
								0.25F / 16,
								(buttonStates[1] ? 2.9F : 3.5F) / 16,
								1F / 16,
								1F / 16,
								0,
								0,
								1,
								1,
								facing,
								0xFFFFFFFF,
								light
						);
						// 弹出当前图形状态
						graphicsHolder.pop();
					}

			);


		}

		// 第二个按钮的渲染逻辑
		if (buttonStates[1]) {
			MainRenderer.scheduleRender(new Identifier(Init.MOD_ID, "textures/block/black.png"), false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
				storedMatrixTransformations3.transform(graphicsHolder, offset);
				IDrawing.drawTexture(graphicsHolder, 0.15F, (buttonStates[0] ? -4.2F : -4.8F) / 16, 0.2F, 0.1F, Direction.UP, light);
				graphicsHolder.pop();
			});
			// 根据按钮的按下状态和鼠标位置选择不同的渲染层
			MainRenderer.scheduleRender(
					BUTTON_TEXTURE,
					false,
					buttonStates[3] || lookingAtTopHalf ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.EXTERIOR,
					(graphicsHolder, offset) -> {
						// 应用存储的矩阵变换
						storedMatrixTransformations2.transform(graphicsHolder, offset);
						// 绘制按钮纹理，位置和颜色根据按钮状态和鼠标位置决定
						IDrawing.drawTexture(
								graphicsHolder,
								-1.25F / 16,
								(buttonStates[0] ? 4.1F : 3.5F) / 16,
								1F / 16,
								1F / 16,
								0,
								1,
								1,
								0,
								facing,
								buttonStates[3] ? PRESSED_COLOR : lookingAtTopHalf ? HOVER_COLOR : 0xFFFFFFFF,
								light
						);

						// 弹出当前图形状态
						graphicsHolder.pop();
					}
			);
			MainRenderer.scheduleRender(
					ARROW_TEXTURE,
					false,
					QueuedRenderLayer.EXTERIOR,
					(graphicsHolder, offset) -> {
						// 应用存储的矩阵变换
						storedMatrixTransformations2.transform(graphicsHolder, offset);
						// 绘制按钮纹理，位置和颜色根据按钮状态和鼠标位置决定
						IDrawing.drawTexture(
								graphicsHolder,
								0.25F / 16,
								(buttonStates[0] ? 4.1F : 3.5F) / 16,
								1F / 16,
								1F / 16,
								0,
								1,
								1,
								0,
								facing,
								0xFFFFFFFF,
								light
						);

						// 弹出当前图形状态
						graphicsHolder.pop();
					}
			);
		}
	}
}
