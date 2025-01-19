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
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.Init;
import top.xfunny.block.SchindlerSSeriesGreyButton;
import top.xfunny.block.base.LiftButtonsBase;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;
import top.xfunny.view.*;
import top.xfunny.view.view_group.FrameLayout;

public class RenderSchindlerSSeriesGreyButton extends BlockEntityRenderer<SchindlerSSeriesGreyButton.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFF90FF90;
    private static final int PRESSED_COLOR = 0xFF00FF00;
    private final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_s_series_grey_button.png");
    private final Identifier BUTTON_LIGHT_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_s_series_grey_button_light.png");


    public RenderSchindlerSSeriesGreyButton(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SchindlerSSeriesGreyButton.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
            graphicsHolder.translate(0, 0, 0.062 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 3.5 / 16, (float) 9 / 16);
        parentLayout.setPosition((float) -0.109375, 0);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);


        LiftButtonView button = new LiftButtonView();
        button.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false,false);
        button.setLight(light);
        button.setDefaultColor(0xFFFFFFFF);
        button.setHover(false);
        button.setPressedColor(0xFFFFFFFF);
        button.setHoverColor(0xFFFFFFFF);
        button.setTexture(BUTTON_TEXTURE, true);
        button.setWidth(2F / 16);
        button.setHeight(2F / 16);
        button.setSpacing(0.1F / 16);
        button.setGravity(Gravity.CENTER);

        LiftButtonView buttonLight = new LiftButtonView();
        buttonLight.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false,false);
        buttonLight.setLight(light);
        buttonLight.setDefaultColor(0xFF000000);
        buttonLight.setHover(true);
        buttonLight.setPressedColor(PRESSED_COLOR);
        buttonLight.setHoverColor(HOVER_COLOR);
        buttonLight.setTexture(BUTTON_LIGHT_TEXTURE, true);
        buttonLight.setWidth(2F / 16);
        buttonLight.setHeight(2F / 16);
        buttonLight.setSpacing(0.1F / 16);
        buttonLight.setGravity(Gravity.CENTER);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            SchindlerSSeriesGreyButton.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
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
