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
import top.xfunny.mod.block.SchindlerMSeriesScreen2Even;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ClientGetLiftDetails;

import java.util.Comparator;

import static org.mtr.core.data.LiftDirection.NONE;

public class RenderSchindlerMSeriesScreen2<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR = 0xFFFFCC00;
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/schindler_m_series_panel_arrow_1.png");
    private final boolean isOdd;

    public RenderSchindlerMSeriesScreen2(Argument dispatcher, Boolean isOdd) {
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
            graphicsHolder.translate(0, 0, 7.9F / 16 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions( 18F / 16, 3F / 16);
        parentLayout.setPosition(isOdd ? -0.5625F : -1.0625F, 0.5625F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        final LinearLayout linearLayout = new LinearLayout(false);
        linearLayout.setBasicsAttributes(world, blockEntity.getPos2());
        linearLayout.setHeight(LayoutSize.WRAP_CONTENT);
        linearLayout.setWidth(LayoutSize.WRAP_CONTENT);
        linearLayout.setGravity(Gravity.CENTER);

        final FrameLayout screenLayout = new FrameLayout();
        screenLayout.setBasicsAttributes(world, blockEntity.getPos2());
        screenLayout.setWidth(LayoutSize.WRAP_CONTENT);
        screenLayout.setHeight(LayoutSize.WRAP_CONTENT);
        screenLayout.setGravity(Gravity.CENTER_VERTICAL);
        screenLayout.setBackgroundColor(0xFF000000);
        screenLayout.setMargin(0, 0, 1.5F/16, 0);

        ButtonView upLantern = new ButtonView();
        upLantern.setBasicsAttributes(world, blockEntity.getPos2());
        upLantern.setTexture(BUTTON_TEXTURE);
        upLantern.setDimension(2F / 16);
        upLantern.setGravity(Gravity.CENTER_VERTICAL);
        upLantern.setLight(light);
        upLantern.setDefaultColor(DEFAULT_COLOR);
        upLantern.setPressedColor(PRESSED_COLOR);
        upLantern.setMargin(0, 0, 1.5F/16, 0);

        ButtonView upLantern1  = new ButtonView();
        upLantern1.setBasicsAttributes(world, blockEntity.getPos2());
        upLantern1.setTexture(BUTTON_TEXTURE);
        upLantern1.setDimension(2F / 16);
        upLantern1.setGravity(Gravity.CENTER_VERTICAL);
        upLantern1.setLight(light);
        upLantern1.setDefaultColor(DEFAULT_COLOR);
        upLantern1.setPressedColor(PRESSED_COLOR);

        ButtonView downLantern  = new ButtonView();
        downLantern.setBasicsAttributes(world, blockEntity.getPos2());
        downLantern.setTexture(BUTTON_TEXTURE);
        downLantern.setDimension(2F / 16);
        downLantern.setGravity(Gravity.CENTER_VERTICAL);
        downLantern.setLight(light);
        downLantern.setDefaultColor(DEFAULT_COLOR);
        downLantern.setPressedColor(PRESSED_COLOR);
        downLantern.setFlip(false,true);
        downLantern.setMargin(0, 0, 1.5F/16, 0);

        ButtonView downLantern1  = new ButtonView();
        downLantern1.setBasicsAttributes(world, blockEntity.getPos2());
        downLantern1.setTexture(BUTTON_TEXTURE);
        downLantern1.setDimension(2F / 16);
        downLantern1.setGravity(Gravity.CENTER_VERTICAL);
        downLantern1.setLight(light);
        downLantern1.setDefaultColor(DEFAULT_COLOR);
        downLantern1.setPressedColor(PRESSED_COLOR);
        downLantern1.setFlip(false,true);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            SchindlerMSeriesScreen2Even.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));

                LiftDirection pressedButtonDirection = blockEntity.getPressedButtonDirection();

                ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, org.mtr.mod.Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
                String floorNumber = liftDetails.right().left();
                String currentFloorNumber = RenderLifts.getLiftDetails(world, lift, trackPosition).right().left();

                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);

                if (instructionDirections.isEmpty() && pressedButtonDirection != null && lift.getDoorValue() != 0 && floorNumber.equals(currentFloorNumber)) {
                    switch (pressedButtonDirection) {
                        case DOWN:
                            downLantern.activate();
                            downLantern1.activate();
                            break;
                        case UP:
                            upLantern.activate();
                            upLantern1.activate();
                            break;
                    }
                }

                instructionDirections.forEach(liftDirection -> {
                    if (lift.getDoorValue() != 0 && floorNumber.equals(currentFloorNumber)) {
                        if (liftDirection == NONE) {
                            if (pressedButtonDirection != null) {
                                switch (pressedButtonDirection) {
                                    case DOWN:
                                        downLantern.activate();
                                        downLantern1.activate();
                                        break;
                                    case UP:
                                        upLantern.activate();
                                        upLantern1.activate();
                                        break;
                                }
                            }
                        } else {
                            switch (liftDirection) {
                                case DOWN:
                                    downLantern.activate();
                                    downLantern1.activate();
                                    break;
                                case UP:
                                    upLantern.activate();
                                    upLantern1.activate();
                                    break;
                            }
                        }
                    }

                });
            });
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockEntity.getPos2().getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));



        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = 1;

            for (int i = 0; i < count; i++) {
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockEntity.getPos2(),
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("acmeled"),//字体
                        11,//字号
                        0xFFFF0000);//字体颜色
                liftFloorDisplayView.setDisplayLength(2, 0);//true开启滚动，开启滚动时的字数条件(>)，滚动速度
                liftFloorDisplayView.setTextureId("schindler_m_series_screen_2_display");//字体贴图id，不能与其他显示屏的重复
                liftFloorDisplayView.setWidth(2F / 16);//显示屏宽度
                liftFloorDisplayView.setHeight(2F / 16);//显示屏高度
                liftFloorDisplayView.setGravity(Gravity.CENTER);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);//文字对齐方式，center为居中对齐，left为左对齐，right为右对齐
                liftFloorDisplayView.setMargin(0.6F / 16, 0, 0.6F / 16, 0);

                screenLayout.addChild(liftFloorDisplayView);
            }
        }

        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });

        if (buttonDescriptor.hasDownButton() && buttonDescriptor.hasUpButton()) {
            linearLayout.addChild(downLantern);
            linearLayout.addChild(screenLayout);
            linearLayout.addChild(upLantern1);
        } else if (buttonDescriptor.hasDownButton()) {
            linearLayout.addChild(downLantern);
            linearLayout.addChild(screenLayout);
            linearLayout.addChild(downLantern1);
        } else if (buttonDescriptor.hasUpButton()) {
            linearLayout.addChild(upLantern);
            linearLayout.addChild(screenLayout);
            linearLayout.addChild(upLantern1);
        }


        parentLayout.addChild(linearLayout);
        parentLayout.render();
    }
}
