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
import top.xfunny.mod.block.OtisSPEC60Button1;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.DefaultButtonsKeyMapping;
import top.xfunny.mod.util.ReverseRendering;

import java.util.Comparator;

public class RenderOtisSPEC60Button1 extends BlockEntityRenderer<OtisSPEC60Button1.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFFFCC66;
    private static final int PRESSED_COLOR = 0xFFFF0000;
    private static final Identifier ARROW_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/otis_spec_60_arrow_1.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/otis_ak10_button_1.png");
    private static final Identifier BUTTON_LIGHT_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/otis_ak10_button_1_light.png");

    public RenderOtisSPEC60Button1(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(OtisSPEC60Button1.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) {
            return;
        }

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) {
            return;
        }

        final DefaultButtonsKeyMapping keyMapping = blockEntity.getKeyMapping();

        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false, false);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 7.9F / 16 - SMALL_OFFSET);
        });


        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(4F / 16, 12F / 16);
        parentLayout.setPosition(-0.125F, 0.0625F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LinearLayout screenLayout = new LinearLayout(false);
        screenLayout.setBasicsAttributes(world, blockPos);
        screenLayout.setWidth(LayoutSize.WRAP_CONTENT);
        screenLayout.setHeight(LayoutSize.WRAP_CONTENT);
        screenLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        screenLayout.setMargin(0, 0.7F / 16, 0, 0);

        final FrameLayout buttonLayout = new FrameLayout();
        buttonLayout.setBasicsAttributes(world, blockPos);
        buttonLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonLayout.setHeight(LayoutSize.MATCH_PARENT);
        buttonLayout.setMargin(0, 0.45F / 16, 0, 0);

        final LinearLayout buttonContainer = new LinearLayout(true);
        buttonContainer.setBasicsAttributes(world, blockPos);
        buttonContainer.setWidth(LayoutSize.WRAP_CONTENT);
        buttonContainer.setHeight(LayoutSize.WRAP_CONTENT);
        buttonContainer.setGravity(Gravity.CENTER);

        final FrameLayout upButtonGroup = new FrameLayout();
        upButtonGroup.setBasicsAttributes(world, blockPos);
        upButtonGroup.setStoredMatrixTransformations(storedMatrixTransformations1);
        upButtonGroup.setWidth(LayoutSize.WRAP_CONTENT);
        upButtonGroup.setHeight(LayoutSize.WRAP_CONTENT);
        upButtonGroup.setGravity(Gravity.CENTER_HORIZONTAL);

        final FrameLayout downButtonGroup = new FrameLayout();
        downButtonGroup.setBasicsAttributes(world, blockPos);
        downButtonGroup.setStoredMatrixTransformations(storedMatrixTransformations1);
        downButtonGroup.setWidth(LayoutSize.WRAP_CONTENT);
        downButtonGroup.setHeight(LayoutSize.WRAP_CONTENT);
        downButtonGroup.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView buttonUp = new ImageView();
        buttonUp.setBasicsAttributes(world, blockPos);
        buttonUp.setTexture(BUTTON_TEXTURE);
        buttonUp.setDimension(0.9F / 16);
        buttonUp.setGravity(Gravity.CENTER);
        buttonUp.setLight(light);

        ButtonView buttonUpLight = new ButtonView();
        buttonUpLight.setId("up");
        buttonUpLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonUpLight.setTexture(BUTTON_LIGHT_TEXTURE);
        buttonUpLight.setDimension(0.9F / 16);
        buttonUpLight.setGravity(Gravity.CENTER);
        buttonUpLight.setLight(light);
        buttonUpLight.setDefaultColor(ARGB_WHITE);
        buttonUpLight.setHoverColor(HOVER_COLOR);
        buttonUpLight.setPressedColor(PRESSED_COLOR);

        ImageView buttonDown = new ImageView();
        buttonDown.setBasicsAttributes(world, blockPos);
        buttonDown.setTexture(BUTTON_TEXTURE);
        buttonDown.setDimension(0.9F / 16);
        buttonDown.setGravity(Gravity.CENTER);
        buttonDown.setLight(light);
        buttonDown.setFlip(false, true);

        ButtonView buttonDownLight = new ButtonView();
        buttonDownLight.setId("down");
        buttonDownLight.setBasicsAttributes(world, blockPos, keyMapping);
        buttonDownLight.setTexture(BUTTON_LIGHT_TEXTURE);
        buttonDownLight.setDimension(0.9F / 16);
        buttonDownLight.setGravity(Gravity.CENTER);
        buttonDownLight.setLight(light);
        buttonDownLight.setDefaultColor(ARGB_WHITE);
        buttonDownLight.setHoverColor(HOVER_COLOR);
        buttonDownLight.setPressedColor(PRESSED_COLOR);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);


        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();


        blockEntity.forEachTrackPosition(trackPosition -> {

            line.RenderLine(holdingLinker, trackPosition);


            OtisSPEC60Button1.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            buttonDownLight.activate();
                            break;
                        case UP:
                            buttonUpLight.activate();
                            break;
                    }
                });
            });
        });


        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {

            final int count = Math.min(2, sortedPositionsAndLifts.size());
            final boolean reverseRendering = count > 1 && ReverseRendering.reverseRendering(facing.rotateYCounterclockwise(), sortedPositionsAndLifts.get(0).left(), sortedPositionsAndLifts.get(1).left());


            for (int i = 0; i < count; i++) {

                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("otis_series1"),
                        4,
                        0xFFFF0000);
                liftFloorDisplayView.setDisplayLength(2, 0.05F);
                liftFloorDisplayView.setTextureId("otis_spec_60_button_1_display");
                liftFloorDisplayView.setWidth(1.5F / 16);
                liftFloorDisplayView.setLetterSpacing(10);
                liftFloorDisplayView.setHeight(1.7F / 16);
                liftFloorDisplayView.setMargin(-0.225F / 16, -0.3F / 16, 0.3F / 16, 0);

                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);

                final LinearLayout arrowLayout = new LinearLayout(false);
                arrowLayout.setBasicsAttributes(world, blockPos);
                arrowLayout.setWidth(LayoutSize.WRAP_CONTENT);
                arrowLayout.setHeight(LayoutSize.WRAP_CONTENT);
                arrowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

                final LiftArrowView liftArrowViewUP = new LiftArrowView();
                liftArrowViewUP.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.UP);
                liftArrowViewUP.setTexture(ARROW_TEXTURE);
                liftArrowViewUP.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                liftArrowViewUP.setDimension(0.5F / 16);
                liftArrowViewUP.setMargin(0, 4.07F / 16, 0, 0);
                liftArrowViewUP.setGravity(Gravity.CENTER_HORIZONTAL);
                liftArrowViewUP.setColor(0xFFFF0000);

                final LiftArrowView liftArrowViewDown = new LiftArrowView();
                liftArrowViewDown.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.DOWN);
                liftArrowViewDown.setTexture(ARROW_TEXTURE);
                liftArrowViewDown.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                liftArrowViewDown.setDimension(0.5F / 16);
                liftArrowViewDown.setMargin(0, 4.07F / 16, 0, 0);
                liftArrowViewDown.setGravity(Gravity.CENTER_HORIZONTAL);
                liftArrowViewDown.setColor(0xFFFF0000);

                arrowLayout.addChild(liftArrowViewDown);
                arrowLayout.addChild(liftArrowViewUP);

                final LinearLayout numberLayout = new LinearLayout(true);
                numberLayout.setBasicsAttributes(world, blockPos);
                numberLayout.setWidth(LayoutSize.WRAP_CONTENT);
                numberLayout.setHeight(LayoutSize.WRAP_CONTENT);

                numberLayout.addChild(arrowLayout);
                numberLayout.addChild(liftFloorDisplayView);

                if (reverseRendering) {
                    screenLayout.addChild(numberLayout);
                    screenLayout.reverseChildren();
                } else {
                    screenLayout.addChild(numberLayout);
                }
            }
        }

        upButtonGroup.addChild(buttonUp);
        upButtonGroup.addChild(buttonUpLight);
        downButtonGroup.addChild(buttonDown);
        downButtonGroup.addChild(buttonDownLight);

        if (buttonDescriptor.hasUpButton()) {
            buttonContainer.addChild(upButtonGroup);
        }

        if (buttonDescriptor.hasDownButton()) {
            if (buttonDescriptor.hasUpButton()) {
                downButtonGroup.setMargin(0, 0.7F / 16, 0, 0);
            } else {
                downButtonGroup.setMargin(0, 1.6F / 16, 0, 0);
            }
            buttonContainer.addChild(downButtonGroup);
        }

        buttonLayout.addChild(buttonContainer);
        parentLayout.addChild(screenLayout);
        parentLayout.addChild(buttonLayout);

        parentLayout.render();
    }
}