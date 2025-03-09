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
import org.mtr.mod.render.RenderLifts;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.Init;
import top.xfunny.block.MitsubishiNexWayScreen1Even;
import top.xfunny.block.SchindlerMSeriesScreen3Even;
import top.xfunny.block.base.LiftButtonsBase;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;
import top.xfunny.resource.FontList;
import top.xfunny.util.ClientGetLiftDetails;
import top.xfunny.view.*;
import top.xfunny.view.view_group.FrameLayout;

import java.util.Comparator;

import static org.mtr.core.data.LiftDirection.NONE;

public class RenderSchindlerMSeriesScreen3<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR = 0xFFFFCC00;
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_m_series_panel_arrow_2.png");
    private static final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_m_series_lantern_3.png");
    private final boolean isOdd;

    public RenderSchindlerMSeriesScreen3(Argument dispatcher, Boolean isOdd) {
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
            graphicsHolder.translate(0, 0, 0.0597 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 18 / 16, (float) 3 / 16);
        parentLayout.setPosition(isOdd ? (float) -0.5575 : (float) -1.0625, (float) 0.61);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final FrameLayout screenLayout = new FrameLayout();
        screenLayout.setBasicsAttributes(world, blockEntity.getPos2());
        screenLayout.setWidth(LayoutSize.WRAP_CONTENT);
        screenLayout.setHeight(LayoutSize.WRAP_CONTENT);
        screenLayout.setGravity(Gravity.CENTER);

        LiftButtonView button = new LiftButtonView();
        button.setBasicsAttributes(world, blockEntity.getPos2(), buttonDescriptor, false, true, true);
        button.setLight(light);
        button.setHover(false);
        button.setDefaultColor(0xFFFFFFFF);
        button.setPressedColor(PRESSED_COLOR);
        button.setHoverColor(0xFFFFFFFF);
        button.setTexture(BUTTON_TEXTURE, true);
        button.setWidth(3.25F / 16);
        button.setHeight(3.25F / 16);
        button.setSpacing(6.45F / 16);
        button.setGravity(Gravity.CENTER);

        LiftButtonView button1 = new LiftButtonView();
        button1.setBasicsAttributes(world, blockEntity.getPos2(), buttonDescriptor, false, true, true);
        button1.setLight(light);
        button1.setHover(false);
        button1.setDefaultColor(0xFFFFFFFF);
        button1.setPressedColor(0xFFFFFFFF);
        button1.setHoverColor(0xFFFFFFFF);
        button1.setTexture(ARROW_TEXTURE, true);
        button1.setWidth(3.25F / 16);
        button1.setHeight(3.25F / 16);
        button1.setSpacing(6.45F / 16);
        button1.setGravity(Gravity.CENTER);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            SchindlerMSeriesScreen3Even.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
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
                            break;
                        case UP:
                            button.setUpButtonLight();
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
                                        break;
                                    case UP:
                                        button.setUpButtonLight();
                                        break;
                                }
                            }
                        } else {
                            switch (liftDirection) {
                                case DOWN:
                                    button.setDownButtonLight();
                                    break;
                                case UP:
                                    button.setUpButtonLight();
                                    break;
                            }
                        }
                    }

                });
            });
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockEntity.getPos2().getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            // 确定要渲染的电梯数量，这里设置为2个
            final int count = 1;

            for (int i = 0; i < count; i++) {
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockEntity.getPos2(),
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("acmeled"),//字体
                        6,//字号
                        0xFFFF0000);//字体颜色
                liftFloorDisplayView.setTextScrolling(true, 2, 0);//true开启滚动，开启滚动时的字数条件(>)，滚动速度
                liftFloorDisplayView.setTextureId("schindler_m_series_screen_3_display");//字体贴图id，不能与其他显示屏的重复
                liftFloorDisplayView.setWidth((float) 2 / 16);//显示屏宽度
                liftFloorDisplayView.setHeight((float) 2 / 16);//显示屏高度
                liftFloorDisplayView.setGravity(Gravity.CENTER);
                liftFloorDisplayView.setTextAlign(LiftFloorDisplayView.TextAlign.RIGHT);//文字对齐方式，center为居中对齐，left为左对齐，right为右对齐
                liftFloorDisplayView.setMargin((float) 0.6 / 16, 0, (float) 0.6 / 16, 0);

                screenLayout.addChild(liftFloorDisplayView);
            }
        }

        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });

        parentLayout.addChild(screenLayout);
        parentLayout.addChild(button);
        parentLayout.addChild(button1);

        parentLayout.render();
    }
}
