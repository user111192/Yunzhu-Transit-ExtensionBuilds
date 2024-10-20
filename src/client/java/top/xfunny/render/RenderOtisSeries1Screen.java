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
import org.mtr.mod.Init;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.block.OtisSeries1Screen;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;
import top.xfunny.resource.TextureList;
import top.xfunny.util.ClientGetLiftDetails;

import java.util.Comparator;

public class RenderOtisSeries1Screen extends BlockEntityRenderer<OtisSeries1Screen.BlockEntity> implements DirectionHelper, IGui, IBlock {
	public RenderOtisSeries1Screen(Argument dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(OtisSeries1Screen.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay){
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
		final StoredMatrixTransformations storedMatrixTransformations1 = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);

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
			//检测是否安装电梯
			OtisSeries1Screen.LiftCheck(trackPosition, (floorIndex, lift) -> {
				sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
			});
		});

		// Sort list and only render the two closest lifts
		sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));
		final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations1.copy();
		storedMatrixTransformations2.add(graphicsHolder -> {
			graphicsHolder.rotateYDegrees(-facing.asRotation());
			graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
		});

		// 检查排序后的电梯位置列表是否非空
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
			MainRenderer.scheduleRender(new Identifier(Init.MOD_ID, "textures/block/black.png"), false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
				storedMatrixTransformations3.transform(graphicsHolder, offset);
				IDrawing.drawTexture(graphicsHolder, 0.0125F, -0.365F, 0.225F, 0.225F, Direction.UP, light);
				graphicsHolder.pop();
			});


			// 遍历要渲染的每个电梯
			for (int i = 0; i < count; i++) {
				// 计算当前电梯显示的x位置，考虑反转渲染顺序
				final double x = (i + 0.5) * width / count;
				// 创建另一个矩阵变换的副本用于当前电梯
				final StoredMatrixTransformations storedMatrixTransformations4 = storedMatrixTransformations3.copy();
				// 添加平移变换以定位电梯显示
				storedMatrixTransformations4.add(graphicsHolder -> graphicsHolder.translate(x, -0.875, -SMALL_OFFSET));

				// 渲染当前电梯的显示
				renderLiftDisplay(storedMatrixTransformations4, world, sortedPositionsAndLifts.get(i).right(), width * 4  / count,0.2F);

			}
		}
	}
	private void renderLiftDisplay(StoredMatrixTransformations storedMatrixTransformations, World world , Lift lift ,float width,float height) {
		// 获取电梯的详细信息，包括运行方向和楼层信息
		final ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
		final String floorNumber = liftDetails.right().left();
		final String floorDescription = liftDetails.right().right();

		// 判断楼层编号和描述是否为空
		final boolean noFloorNumber = floorNumber.isEmpty();
		final boolean noFloorDisplay = floorDescription.isEmpty();
		final float y = height; // 箭头的Y轴位置




		// 渲染楼层信息
		if (!noFloorNumber || !noFloorDisplay) {
			final String text = String.format("%s%s", floorNumber, noFloorNumber? " " : ""); // 合并楼层信息文本
			int totalWidth = TextureList.instance.getOtisSeries1ScreenDisplay(text, 0x1d953f).width;
			int maxWidth = text.length()>=2? 210:104;
			float textureWidth = text.length()>=2? 0.13F:0.13F/2;
			float x =text.length()>=2?-width+0.98F:-width+1.04F;
			MainRenderer.scheduleRender(TextureList.instance.getOtisSeries1ScreenDisplay(text, 0x1d953f).identifier, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
				storedMatrixTransformations.transform(graphicsHolder, offset);

				IDrawing.drawTexture(graphicsHolder, x - 0.04F, y + 0.365F, textureWidth, 0.12F, 0, 0, (float) maxWidth /totalWidth, 1, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());

				graphicsHolder.pop();
			});

		}
	}
}
