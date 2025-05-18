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
import org.mtr.mod.render.RenderLifts;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.SchindlerMSeriesRoundLantern1Even;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.util.ClientGetLiftDetails;
import top.xfunny.mod.client.view.Gravity;
import top.xfunny.mod.client.view.LayoutSize;
import top.xfunny.mod.client.view.LiftButtonView;
import top.xfunny.mod.client.view.LineComponent;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import static org.mtr.core.data.LiftDirection.NONE;

public class RenderOtisSeries3Lantern1Arrow<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR_UP = 0xFF00FF00;
    private static final int PRESSED_COLOR_DOWN = 0xFFFF0000;
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/otis_s3_lantern_1_arrow.png");
    private final boolean isOdd;

    public RenderOtisSeries3Lantern1Arrow(Argument dispatcher, Boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    @Override
    public void render(T blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
            graphicsHolder.translate(0, 0, 0.046 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 7.5 / 16, (float) 7.5 / 16);
        parentLayout.setPosition(isOdd ? (float) -3.75 / 16 : (float) -11.75 / 16, (float) 4.25 / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        LiftButtonView button = new LiftButtonView();
        button.setBasicsAttributes(world, blockEntity.getPos2(), buttonDescriptor, true, false, true, true);
        button.setLight(light);
        button.setHover(false);
        button.setDefaultColor(0xFF222222);
        button.setHoverColor(0xFF222222);
        button.setTexture(BUTTON_TEXTURE, true);
        button.setWidth(1.2F / 16);
        button.setHeight(1.2F / 16);
        button.setSpacing(1.1F / 16);
        button.setGravity(Gravity.CENTER);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            SchindlerMSeriesRoundLantern1Even.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));

                LiftDirection pressedButtonDirection = blockEntity.getPressedButtonDirection();

                ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, org.mtr.mod.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
                String floorNumber = liftDetails.right().left();
                String currentFloorNumber = RenderLifts.getLiftDetails(world, lift, trackPosition).right().left();

                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);

                if (instructionDirections.isEmpty() && pressedButtonDirection != null && lift.getDoorValue() != 0 && floorNumber.equals(currentFloorNumber)) {
                    switch (pressedButtonDirection) {
                        case DOWN:
                            button.setDownButtonLight();
                            button.setPressedColor(PRESSED_COLOR_DOWN);
                            break;
                        case UP:
                            button.setUpButtonLight();
                            button.setPressedColor(PRESSED_COLOR_UP);
                            break;
                    }
                }

                instructionDirections.forEach(liftDirection -> {
                    if (lift.getDoorValue() != 0 && floorNumber.equals(currentFloorNumber)) {
                        if (liftDirection == NONE) {
                            if (pressedButtonDirection != null) {
                                switch (pressedButtonDirection) {
                                    case DOWN:
                                        button.setDownButtonLight();
                                        button.setPressedColor(PRESSED_COLOR_DOWN);
                                        break;
                                    case UP:
                                        button.setUpButtonLight();
                                        button.setPressedColor(PRESSED_COLOR_UP);
                                        break;
                                }
                            }
                        } else {
                            switch (liftDirection) {
                                case DOWN:
                                    button.setDownButtonLight();
                                    button.setPressedColor(PRESSED_COLOR_DOWN);
                                    break;
                                case UP:
                                    button.setUpButtonLight();
                                    button.setPressedColor(PRESSED_COLOR_UP);
                                    break;
                            }
                        }
                    }

                });
            });
        });

        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });
        parentLayout.addChild(button);

        parentLayout.render();
    }
}
