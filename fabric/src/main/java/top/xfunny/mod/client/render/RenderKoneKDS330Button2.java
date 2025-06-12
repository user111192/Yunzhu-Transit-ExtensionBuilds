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
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.KoneKDS330Button2;
import top.xfunny.mod.block.SchindlerMSeriesButton;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.Gravity;
import top.xfunny.mod.client.view.LayoutSize;
import top.xfunny.mod.client.view.LiftButtonView;
import top.xfunny.mod.client.view.LineComponent;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

public class RenderKoneKDS330Button2 extends BlockEntityRenderer<KoneKDS330Button2.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private final int HOVER_COLOR = 0xFFFFFFFF;
    private final int PRESSED_COLOR = 0xFFFFFFFF;
    private final Identifier BUTTON_TEXTURE_UP = new Identifier(Init.MOD_ID, "textures/block/kone_kds330_up_button.png");
    private final Identifier BUTTON_TEXTURE_DOWN = new Identifier(Init.MOD_ID, "textures/block/kone_kds330_down_button_b.png");
    private final Identifier BUTTON_LIGHT_TEXTURE_UP = new Identifier(Init.MOD_ID, "textures/block/kone_kds330_up_button_light.png");
    private final Identifier BUTTON_LIGHT_TEXTURE_DOWN = new Identifier(Init.MOD_ID, "textures/block/kone_kds330_down_button_light.png");

    public RenderKoneKDS330Button2(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(KoneKDS330Button2.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        // 创建一个存储矩阵转换的实例，用于后续的渲染操作
        // 参数为方块的中心位置坐标 (x, y, z)
        final StoredMatrixTransformations storedMatrixTransformations1 = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.5F / 16 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 6 / 16, (float) 8 / 16);
        parentLayout.setPosition((float) -0.1875, 0.0265F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LiftButtonView button = new LiftButtonView();
        button.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, false, false);
        button.setLight(255);
        button.setHover(false);
        button.setDefaultColor(0xFFFFFFFF);
        button.setPressedColor(0xFFFFFFFF);//按钮按下时颜色
        button.setHoverColor(0xFFFFFFFF);//准星瞄准时的颜色
        button.setTexture(BUTTON_TEXTURE_UP, BUTTON_TEXTURE_DOWN, true);//按钮贴图
        button.setWidth(0.9F / 16);//按钮宽度
        button.setHeight(0.9F / 16);//按钮高度
        button.setSpacing(3.565F / 16);//两个按钮的间距
        button.setGravity(Gravity.CENTER);//让按钮在父容器（buttonLayout）中居中

        final LiftButtonView buttonLight = new LiftButtonView();
        buttonLight.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, false, false);
        buttonLight.setLight(light);
        buttonLight.setHover(true);
        buttonLight.setDefaultColor(0xFFFFFFFF);
        buttonLight.setPressedColor(PRESSED_COLOR);
        buttonLight.setHoverColor(HOVER_COLOR);
        buttonLight.setTexture(BUTTON_LIGHT_TEXTURE_UP, BUTTON_LIGHT_TEXTURE_DOWN, false);
        buttonLight.setWidth(0.9F / 16);
        buttonLight.setHeight(0.9F / 16);
        buttonLight.setClientMedian(0.3);
        buttonLight.setSpacing(3.565F / 16);
        buttonLight.setGravity(Gravity.CENTER);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            SchindlerMSeriesButton.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            buttonLight.setDownButtonLight();
                            break;
                        case UP:
                            buttonLight.setUpButtonLight();
                            break;
                    }
                });
            });
        });

        parentLayout.addChild(button);
        parentLayout.addChild(buttonLight);

        parentLayout.render();
    }
}
