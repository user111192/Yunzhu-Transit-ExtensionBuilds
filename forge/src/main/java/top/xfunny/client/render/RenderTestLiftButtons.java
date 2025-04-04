package top.xfunny.client.render;


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
import org.mtr.mod.InitClient;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.block.TestLiftButtons;
import top.xfunny.block.base.LiftButtonsBase;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;
import top.xfunny.client.resource.TextureList;
import top.xfunny.client.util.ClientGetLiftDetails;
import top.xfunny.client.util.ReverseRendering;

import java.util.Comparator;

public class RenderTestLiftButtons extends BlockEntityRenderer<TestLiftButtons.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFADD8E6;
    private static final int PRESSED_COLOR = 0xFF0000FF;
    private static final float ARROW_SPEED = 0.04F;
    private static final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/lift_arrow.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/lift_button.png");

    public RenderTestLiftButtons(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TestLiftButtons.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
        // 数组顺序：向下按钮被按下、向上按钮被按下
        final boolean[] buttonStates = {false, false};
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

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
            TestLiftButtons.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                // 确定是否渲染上下按钮，基于当前trackPosition和楼层信息
                // 该方法通过floorIndex和lift来决定是否添加trackPosition和lift到已排序的列表中
                // 同时，根据lift的方向（上或下），更新buttonStates数组以指示按钮的渲染状态
                // 这里使用lambda表达式来处理按钮状态的逻辑
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));//todo
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            buttonStates[0] = true;
                            break;
                        case UP:
                            buttonStates[1] = true;
                            break;
                    }
                });
            });
        });

        // Sort list and only render the two closest lifts//todo
        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        // Check whether the player is looking at the top or bottom button
        final HitResult hitResult = MinecraftClient.getInstance().getCrosshairTargetMapped();
        final boolean lookingAtTopHalf;
        final boolean lookingAtBottomHalf;
        if (hitResult == null || !IBlock.getStatePropertySafe(blockState, TestLiftButtons.UNLOCKED)) {
            lookingAtTopHalf = false;
            lookingAtBottomHalf = false;
        } else {
            final Vector3d hitLocation = hitResult.getPos();
            final double hitY = MathHelper.fractionalPart(hitLocation.getYMapped());
            final boolean inBlock = hitY < 0.5 && Init.newBlockPos(hitLocation.getXMapped(), hitLocation.getYMapped(), hitLocation.getZMapped()).equals(blockPos);
            lookingAtTopHalf = inBlock && (!buttonDescriptor.hasDownButton() || hitY > 0.25);
            lookingAtBottomHalf = inBlock && (!buttonDescriptor.hasUpButton() || hitY < 0.25);
        }

        final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations1.copy();//todo
        storedMatrixTransformations2.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
        });

        // 根据按钮状态渲染按钮
        // 第一个按钮的渲染逻辑
        if (buttonDescriptor.hasDownButton()) {
            // 根据按钮的按下状态和鼠标位置选择不同的渲染层
            MainRenderer.scheduleRender(
                    BUTTON_TEXTURE,
                    false,
                    buttonStates[0] || lookingAtBottomHalf ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.EXTERIOR,
                    (graphicsHolder, offset) -> {
                        // 应用存储的矩阵变换
                        storedMatrixTransformations2.transform(graphicsHolder, offset);
                        // 绘制按钮纹理，位置和颜色根据按钮状态和鼠标位置决定
                        IDrawing.drawTexture(
                                graphicsHolder,
                                -1.5F / 16,
                                (buttonDescriptor.hasUpButton() ? 0.5F : 2.5F) / 16,
                                3F / 16,
                                3F / 16,
                                0,
                                0,
                                1,
                                1,
                                facing,
                                buttonStates[0] ? PRESSED_COLOR : lookingAtBottomHalf ? HOVER_COLOR : ARGB_GRAY,
                                light
                        );
                        // 弹出当前图形状态
                        graphicsHolder.pop();
                    }
            );
        }
        // 第二个按钮的渲染逻辑
        if (buttonDescriptor.hasUpButton()) {
            // 根据按钮的按下状态和鼠标位置选择不同的渲染层
            MainRenderer.scheduleRender(
                    BUTTON_TEXTURE,
                    false,
                    buttonStates[1] || lookingAtTopHalf ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.EXTERIOR,
                    (graphicsHolder, offset) -> {
                        storedMatrixTransformations2.transform(graphicsHolder, offset);
                        IDrawing.drawTexture(
                                graphicsHolder,
                                -1.5F / 16,
                                (buttonDescriptor.hasDownButton() ? 4.5F : 2.5F) / 16,
                                3F / 16,
                                3F / 16,
                                0,
                                1,
                                1,
                                0,
                                facing,
                                buttonStates[1] ? PRESSED_COLOR : lookingAtTopHalf ? HOVER_COLOR : ARGB_GRAY,
                                light
                        );
                        // 弹出当前图形状态
                        graphicsHolder.pop();
                    }
            );
        }
        // 检查排序后的电梯位置列表是否非空
        if (!sortedPositionsAndLifts.isEmpty()) {
            // 确定要渲染的电梯数量，最多为2个
            final int count = Math.min(2, sortedPositionsAndLifts.size());
            // 设置每个电梯显示的宽度，根据数量不同而变化
            final float width = count == 1 ? 0.25F : 0.495F;

            // 创建当前矩阵变换的副本以供后续修改
            final StoredMatrixTransformations storedMatrixTransformations3 = storedMatrixTransformations2.copy();
            // 添加旋转和平移变换
            storedMatrixTransformations3.add(graphicsHolder -> {
                graphicsHolder.rotateZDegrees(180);
                graphicsHolder.translate(-width / 2, 0, 0);
            });

            // 渲染黑色背景
            MainRenderer.scheduleRender(new Identifier(Init.MOD_ID, "textures/block/white.png"), false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
                storedMatrixTransformations3.transform(graphicsHolder, offset);
                IDrawing.drawTexture(graphicsHolder, 0, -0.9375F, width, 0.40625F, Direction.UP, light);
                graphicsHolder.pop();
            });

            // 根据按钮朝向判断两个最近的电梯是否需要反转渲染顺序//todo:使用layout排序需要反转添加顺序（layout层面的操作）
            final boolean reverseRendering = count > 1 && ReverseRendering.reverseRendering(facing.rotateYCounterclockwise(), sortedPositionsAndLifts.get(0).left(), sortedPositionsAndLifts.get(1).left());
            // 遍历要渲染的每个电梯
            for (int i = 0; i < count; i++) {
                final double x = ((reverseRendering ? count - i - 1 : i) + 0.5) * width / count;
                final StoredMatrixTransformations storedMatrixTransformations4 = storedMatrixTransformations3.copy();
                storedMatrixTransformations4.add(graphicsHolder -> graphicsHolder.translate(x, -0.875, -SMALL_OFFSET));
                // 渲染当前电梯的显示
                renderLiftDisplay(storedMatrixTransformations4, world, sortedPositionsAndLifts.get(i).right(), width * 4 / count, 0.2F, 0.2F, 0.2F);
            }
        }
    }

    private void renderLiftDisplay(StoredMatrixTransformations storedMatrixTransformations, World world, Lift lift, float width, float width1, float height1, float height) {
        // 获取电梯的详细信息，包括运行方向和楼层信息
        final ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
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

        // 渲染电梯运行方向的箭头
        if (liftDirection != LiftDirection.NONE) {
            final float uv = (gameTick * ARROW_SPEED) % 1;
            final int color = goingUp ? 0xFF00FF00 : 0xFFFF0000; // 根据运行方向设置箭头颜色
            MainRenderer.scheduleRender(ARROW_TEXTURE, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
                storedMatrixTransformations.transform(graphicsHolder, offset);
                // 根据电梯运行方向绘制箭头
                IDrawing.drawTexture(graphicsHolder, -width / 4 + arrowSize, y - 0.24F, arrowSize, arrowSize, 0, (goingUp ? 0 : 1) + uv, 1, (goingUp ? 1 : 0) + uv, Direction.UP, color, GraphicsHolder.getDefaultLight());
                graphicsHolder.pop();
            });
        }
        // 渲染楼层信息
        if (!noFloorNumber || !noFloorDisplay) {
            float offset1;
            final String text = String.format("%s%s", floorNumber, noFloorNumber ? " " : "");
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

                    IDrawing.drawTexture(graphicsHolder, -width + 0.9F, y - 0.07F, width1, height1, finalOffset, 0, finalOffset + (float) 170 / totalWidth, 1, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());//楼层数字尺寸设置
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
