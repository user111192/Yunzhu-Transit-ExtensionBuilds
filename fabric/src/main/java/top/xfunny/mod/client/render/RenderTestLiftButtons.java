package top.xfunny.mod.client.render;

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
import top.xfunny.mod.block.TestLiftButtons;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.resource.TextureList;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ClientGetLiftDetails;
import top.xfunny.mod.util.ReverseRendering;

import java.util.Comparator;

public class RenderTestLiftButtons extends BlockEntityRenderer<TestLiftButtons.BlockEntity> implements DirectionHelper, IGui, IBlock {

    // 炫酷颜色配置
    private static final int HOVER_COLOR = 0x55ADD8E6; // 半透明蓝色
    private static final int PRESSED_COLOR = 0xAA0000FF; // 半透明亮蓝色
    private static final int GLOW_COLOR = 0x88FFFFFF; // 发光效果
    private static final float ARROW_SPEED = 0.08F; // 加快箭头动画速度
    private static final float PULSE_SPEED = 0.05F; // 脉冲效果速度

    // 纹理资源
    private static final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/lift_arrow.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/lift_button.png");
    private static final Identifier GLOW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/glow_effect.png");
    private static final Identifier PANEL_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/panel_tech.png");

    public RenderTestLiftButtons(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TestLiftButtons.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) return;

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) return;

        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity),
                item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);

        // 使用更炫酷的矩阵变换效果
        final StoredMatrixTransformations storedMatrixTransformations1 = new StoredMatrixTransformations(
                blockPos.getX() + 0.5,
                blockPos.getY(),
                blockPos.getZ() + 0.5
        );
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.translate(0, Math.sin(InitClient.getGameTick() * 0.1) * 0.01, 0); // 添加轻微浮动效果
        });

        // 按钮状态
        final boolean[] buttonStates = {false, false};
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        // 电梯位置排序
        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        // 炫酷的连接线效果
        blockEntity.forEachTrackPosition(trackPosition -> {
            if (world.getBlockState(trackPosition).getBlock().data instanceof BlockLiftTrackFloor) {
                final Direction trackFacing = IBlock.getStatePropertySafe(world, trackPosition, FACING);
                RenderLiftObjectLink.RenderLiftObjectLink(
                        storedMatrixTransformations1,
                        new Vector3d(facing.getOffsetX() / 2F, 0.5, facing.getOffsetZ() / 2F),
                        new Vector3d(
                                trackPosition.getX() - blockPos.getX() + trackFacing.getOffsetX() / 2F,
                                trackPosition.getY() - blockPos.getY() + 0.5,
                                trackPosition.getZ() - blockPos.getZ() + trackFacing.getOffsetZ() / 2F
                        ),
                        holdingLinker
                );
            }

            TestLiftButtons.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    if (liftDirection == LiftDirection.DOWN) buttonStates[0] = true;
                    if (liftDirection == LiftDirection.UP) buttonStates[1] = true;
                });
            });
        });

        // 排序电梯位置
        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift ->
                blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))
        ));

        // 鼠标悬停检测
        final HitResult hitResult = MinecraftClient.getInstance().getCrosshairTargetMapped();
        final boolean lookingAtTopHalf, lookingAtBottomHalf;
        if (hitResult == null || !IBlock.getStatePropertySafe(blockState, TestLiftButtons.UNLOCKED)) {
            lookingAtTopHalf = lookingAtBottomHalf = false;
        } else {
            final Vector3d hitLocation = hitResult.getPos();
            final double hitY = MathHelper.fractionalPart(hitLocation.getYMapped());
            final boolean inBlock = hitY < 0.5 && Init.newBlockPos(
                    hitLocation.getXMapped(), hitLocation.getYMapped(), hitLocation.getZMapped()
            ).equals(blockPos);
            lookingAtTopHalf = inBlock && (!buttonDescriptor.hasDownButton() || hitY > 0.25);
            lookingAtBottomHalf = inBlock && (!buttonDescriptor.hasUpButton() || hitY < 0.25);
        }

        // 按钮渲染变换
        final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations1.copy();
        storedMatrixTransformations2.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
        });

        // 炫酷按钮渲染
        renderButton(storedMatrixTransformations2, buttonDescriptor.hasDownButton(),
                buttonStates[0], lookingAtBottomHalf,
                buttonDescriptor.hasUpButton() ? 0.5F : 2.5F,
                false, light);

        renderButton(storedMatrixTransformations2, buttonDescriptor.hasUpButton(),
                buttonStates[1], lookingAtTopHalf,
                buttonDescriptor.hasDownButton() ? 4.5F : 2.5F,
                true, light);

        // 炫酷电梯信息面板
        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = Math.min(2, sortedPositionsAndLifts.size());
            final float width = count == 1 ? 0.25F : 0.495F;

            final StoredMatrixTransformations storedMatrixTransformations3 = storedMatrixTransformations2.copy();
            storedMatrixTransformations3.add(graphicsHolder -> {
                graphicsHolder.rotateZDegrees(180);
                graphicsHolder.translate(-width / 2, 0, 0);
            });

            // 高科技面板背景
            MainRenderer.scheduleRender(PANEL_TEXTURE, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
                storedMatrixTransformations3.transform(graphicsHolder, offset);
                IDrawing.drawTexture(graphicsHolder, 0, -0.9375F, width, 0.40625F, Direction.UP, light);
                // 添加发光边框
                renderGlowEffect(graphicsHolder, width, 0.40625F, light);
                graphicsHolder.pop();
            });

            // 电梯信息显示
            final boolean reverseRendering = count > 1 && ReverseRendering.reverseRendering(
                    facing.rotateYCounterclockwise(),
                    sortedPositionsAndLifts.get(0).left(),
                    sortedPositionsAndLifts.get(1).left()
            );

            for (int i = 0; i < count; i++) {
                final double x = ((reverseRendering ? count - i - 1 : i) + 0.5) * width / count;
                final StoredMatrixTransformations storedMatrixTransformations4 = storedMatrixTransformations3.copy();
                int finalI = i;
                storedMatrixTransformations4.add(graphicsHolder -> {
                    graphicsHolder.translate(x, -0.875, -SMALL_OFFSET);
                    graphicsHolder.scale(1F, 1F, (float) (1 + Math.sin(InitClient.getGameTick() * 0.2 + finalI) * 0.05)); // 添加3D波动效果
                });
                renderLiftDisplay(storedMatrixTransformations4, world, sortedPositionsAndLifts.get(i).right(),
                        width * 4 / count, 0.2F, 0.2F, 0.2F);
            }
        }
    }

    private void renderButton(StoredMatrixTransformations transformations, boolean hasButton,
                              boolean isPressed, boolean isHovered, float yOffset,
                              boolean isUpButton, int light) {
        if (!hasButton) return;

        // 按钮基础颜色
        int buttonColor;
        if (isPressed) buttonColor = PRESSED_COLOR;
        else if (isHovered) buttonColor = HOVER_COLOR;
        else {
            buttonColor = ARGB_GRAY;
        }

        // 按钮发光效果
        MainRenderer.scheduleRender(GLOW_TEXTURE, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
            transformations.transform(graphicsHolder, offset);
            if (isPressed || isHovered) {
                float pulse = (float) (0.7 + Math.sin(InitClient.getGameTick() * PULSE_SPEED) * 0.3);
                IDrawing.drawTexture(
                        graphicsHolder,
                        -2F / 16, (yOffset - 1F) / 16,
                        5F / 16, 5F / 16,
                        0, 0, 1, 1,
                        Direction.UP,
                        (isPressed ? PRESSED_COLOR : HOVER_COLOR) & 0x00FFFFFF | ((int) (pulse * 255) << 24),
                        light
                );
            }
            graphicsHolder.pop();
        });

        // 按钮主体
        MainRenderer.scheduleRender(
                BUTTON_TEXTURE,
                false,
                isPressed || isHovered ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.EXTERIOR,
                (graphicsHolder, offset) -> {
                    transformations.transform(graphicsHolder, offset);
                    IDrawing.drawTexture(
                            graphicsHolder,
                            -1.5F / 16, yOffset / 16,
                            3F / 16, 3F / 16,
                            0, isUpButton ? 1 : 0,
                            1, isUpButton ? 0 : 1,
                            Direction.UP,
                            buttonColor,
                            light
                    );
                    graphicsHolder.pop();
                }
        );
    }

    private void renderGlowEffect(GraphicsHolder graphicsHolder, float width, float height, int light) {
        float pulse = (float) (0.5 + Math.sin(InitClient.getGameTick() * 0.1) * 0.5);
        IDrawing.drawTexture(
                graphicsHolder,
                -0.01F, -0.01F,
                width + 0.02F, height + 0.02F,
                0, 0, 1, 1,
                Direction.UP,
                GLOW_COLOR & 0x00FFFFFF | ((int) (pulse * 88) << 24),
                light
        );
    }

    private void renderLiftDisplay(StoredMatrixTransformations storedMatrixTransformations, World world, Lift lift,
                                   float width, float width1, float height1, float height) {
        final ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails =
                ClientGetLiftDetails.getLiftDetails(world, lift, Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
        final LiftDirection liftDirection = liftDetails.left();
        final String floorNumber = liftDetails.right().left();
        final String floorDescription = liftDetails.right().right();

        final boolean noFloorNumber = floorNumber.isEmpty();
        final boolean noFloorDisplay = floorDescription.isEmpty();
        final float gameTick = InitClient.getGameTick();
        final boolean goingUp = liftDirection == LiftDirection.UP;
        final float arrowSize = width / 6;
        final float y = height;

        // 3D箭头效果
        if (liftDirection != LiftDirection.NONE) {
            final float uv = (gameTick * ARROW_SPEED) % 1;
            final int color = goingUp ? 0xFF00FF00 : 0xFFFF0000;

            // 箭头发光效果
            MainRenderer.scheduleRender(GLOW_TEXTURE, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
                storedMatrixTransformations.transform(graphicsHolder, offset);
                IDrawing.drawTexture(
                        graphicsHolder,
                        -width / 4 + arrowSize - 0.02F, y - 0.24F - 0.02F,
                        arrowSize + 0.04F, arrowSize + 0.04F,
                        0, 0, 1, 1,
                        Direction.UP,
                        (color & 0x00FFFFFF) | 0x55000000,
                        158999999
                );
                graphicsHolder.pop();
            });

            // 箭头主体
            MainRenderer.scheduleRender(ARROW_TEXTURE, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
                storedMatrixTransformations.transform(graphicsHolder, offset);
                IDrawing.drawTexture(
                        graphicsHolder,
                        -width / 4 + arrowSize, y - 0.24F,
                        arrowSize, arrowSize,
                        0, (goingUp ? 0 : 1) + uv,
                        1, (goingUp ? 1 : 0) + uv,
                        Direction.UP,
                        color,
                        GraphicsHolder.getDefaultLight()
                );
                graphicsHolder.pop();
            });
        }

        // 炫酷楼层数字显示
        if (!noFloorNumber || !noFloorDisplay) {
            final String text = String.format("%s%s", floorNumber, noFloorNumber ? " " : "");
            int totalWidth = TextureList.instance.getTestLiftButtonsDisplay(text, 0xFFAA00).width;

            // 数字发光背景
            MainRenderer.scheduleRender(GLOW_TEXTURE, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
                storedMatrixTransformations.transform(graphicsHolder, offset);
                IDrawing.drawTexture(
                        graphicsHolder,
                        -width + 0.88F, y - 0.08F,
                        width1 + 0.04F, height1 + 0.04F,
                        0, 0, 1, 1,
                        Direction.UP,
                        0x22FFFFFF,
                        158999999
                );
                graphicsHolder.pop();
            });

            if (text.length() > 2) {
                float scrollSpeed = 0.01F; // 加快滚动速度
                float offset1 = (gameTick * scrollSpeed) % (totalWidth + width1);
                MainRenderer.scheduleRender(
                        TextureList.instance.getTestLiftButtonsDisplay(text, 0xFFAA00).identifier,
                        false,
                        QueuedRenderLayer.LIGHT_TRANSLUCENT,
                        (graphicsHolder, offset) -> {
                            storedMatrixTransformations.transform(graphicsHolder, offset);
                            IDrawing.drawTexture(
                                    graphicsHolder,
                                    -width + 0.9F, y - 0.07F,
                                    width1, height1,
                                    offset1, 0,
                                    offset1 + (float) 170 / totalWidth, 1,
                                    Direction.UP,
                                    ARGB_WHITE,
                                    GraphicsHolder.getDefaultLight()
                            );
                            graphicsHolder.pop();
                        }
                );
            } else {
                // 静态数字的脉冲效果
                float pulse = (float) (0.8 + Math.sin(gameTick * 0.2) * 0.2);
                MainRenderer.scheduleRender(
                        TextureList.instance.getTestLiftButtonsDisplay(text, 0xFFAA00).identifier,
                        false,
                        QueuedRenderLayer.LIGHT_TRANSLUCENT,
                        (graphicsHolder, offset) -> {
                            storedMatrixTransformations.transform(graphicsHolder, offset);
                            IDrawing.drawTexture(
                                    graphicsHolder,
                                    -width + 0.9F, y - 0.07F,
                                    width1 * pulse, height1 * pulse,
                                    0, 0,
                                    1, 1,
                                    Direction.UP,
                                    ARGB_WHITE | ((int) (pulse * 255) << 24),
                                    GraphicsHolder.getDefaultLight()
                            );
                            graphicsHolder.pop();
                        }
                );
            }
        }
    }
}