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
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.MitsubishiNexWayButton3WithoutScreen;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.client.util.ReverseRendering;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;

import java.util.Comparator;

public class RenderMitsubishiNexWayButton3WithoutScreen extends BlockEntityRenderer<MitsubishiNexWayButton3WithoutScreen.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFFFCC66;
    private static final int PRESSED_COLOR = 0xFFFF8800;
    private static final Identifier ARROW_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/mitsubishi_nexway_1_arrow.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/mitsubishi_nexway_button_1.png");
    private static final Identifier LIGHT_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/mitsubishi_nexway_button_1_light.png");

    public RenderMitsubishiNexWayButton3WithoutScreen(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(MitsubishiNexWayButton3WithoutScreen.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) {
            return;
        }

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) {
            return;
        }

        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.5F/16 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(2.05F / 16,  5.8F / 16);
        parentLayout.setPosition(-1.025F/16,1.1F/16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LiftButtonView button = new LiftButtonView();
        button.setBasicsAttributes(world, blockEntity.getPos2(), buttonDescriptor, true, false, false, false);
        button.setLight(light);
        button.setHover(false);
        button.setDefaultColor(0xFFFFFFFF);
        button.setPressedColor(0xFFFFFFFF);
        button.setHoverColor(0xFFFFFFFF);
        button.setTexture(BUTTON_TEXTURE, true);
        button.setWidth(0.9F / 16);
        button.setHeight(0.9F / 16);
        button.setSpacing(0.5F / 16);
        button.setGravity(Gravity.CENTER);

        final LiftButtonView buttonLight = new LiftButtonView();
        buttonLight.setBasicsAttributes(world, blockEntity.getPos2(), buttonDescriptor, true, false, false, false);
        buttonLight.setLight(light);
        buttonLight.setHover(true);
        buttonLight.setDefaultColor(0xFFFFFFFF);
        buttonLight.setPressedColor(PRESSED_COLOR);
        buttonLight.setHoverColor(HOVER_COLOR);
        buttonLight.setTexture(LIGHT_TEXTURE, true);
        buttonLight.setWidth(0.9F / 16);
        buttonLight.setHeight(0.9F / 16);
        buttonLight.setSpacing(0.5F / 16);
        buttonLight.setGravity(Gravity.CENTER);

        //添加外呼与楼层轨道的连线
        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        // 创建一个对象列表，用于存储排序后的位置和升降机的配对信息
        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        // 遍历每个轨道位置，进行后续处理
        blockEntity.forEachTrackPosition(trackPosition -> {
            //开始渲染外呼与轨道的连线
            line.RenderLine(holdingLinker, trackPosition);

            //判断是否渲染上下按钮
            MitsubishiNexWayButton3WithoutScreen.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            //向下的按钮亮灯
                            buttonLight.setDownButtonLight();
                            break;
                        case UP:
                            //向上的按钮亮灯
                            buttonLight.setUpButtonLight();
                            break;
                    }
                });
            });
        });

        //按距离对数组元素进行排序，使其只渲染最近的两部电梯的信息
        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockEntity.getPos2().getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        parentLayout.addChild(button);
        parentLayout.addChild(buttonLight);

        parentLayout.render();
    }
}
