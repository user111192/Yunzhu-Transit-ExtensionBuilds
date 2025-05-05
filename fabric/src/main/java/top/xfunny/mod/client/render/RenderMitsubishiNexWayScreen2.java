package top.xfunny.mod.client.render;


import org.mtr.core.data.Lift;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import top.xfunny.mod.block.MitsubishiNexWayScreen2Even;
import top.xfunny.mod.block.SchindlerDSeriesScreen1Even;
import top.xfunny.mod.block.base.LiftPanelBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.util.ArrayListToString;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Comparator;

public class RenderMitsubishiNexWayScreen2<T extends LiftPanelBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;

    private String TimeStr;{
        Date day = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 mm:HH ");
        String TimeStr = dateFormat.format(day);
    }

    public RenderMitsubishiNexWayScreen2(Argument dispatcher, Boolean isOdd) {
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


        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 0.059 - SMALL_OFFSET);
        });

        final LinearLayout parentLayout = new LinearLayout(false);
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 7.5 / 16, (float) 5 / 16);
        parentLayout.setPosition(isOdd ? -0.234375F : -0.734375F, 0.5625F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
            MitsubishiNexWayScreen2Even.LiftCheck(trackPosition, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
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
                        FontList.instance.getFont("gill_sans_mt"),
                        9,
                        0xFFFFFFFF);
                liftFloorDisplayView.setTextureId("mitsubishi_nexway_screen_2_display");
                liftFloorDisplayView.setWidth((float) 2.8 / 16);
                liftFloorDisplayView.setHeight((float) 2.8 / 16);
                liftFloorDisplayView.setGravity(Gravity.CENTER_VERTICAL);
                liftFloorDisplayView.setTextAlign(LiftFloorDisplayView.TextAlign.CENTER);
                liftFloorDisplayView.setLetterSpacing(0);
                liftFloorDisplayView.setTextScrolling(true, 2, 0);
                liftFloorDisplayView.setMargin((float) -0.35 / 16, 0, (float) 1.7 / 16, 0);
                liftFloorDisplayView.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -SMALL_OFFSET));

                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockEntity.getPos2(), sortedPositionsAndLifts.get(i).right());
                liftArrowView.setTexture(new Identifier(Init.MOD_ID, "textures/block/mitsubishi_nexway_2_lcd_arrow_1.png"));
                liftArrowView.setArrowScrolling(false, 0.05F);
                liftArrowView.setWidth((float) 1.5 / 16);
                liftArrowView.setHeight((float) 1.5 / 16);
                liftArrowView.setMargin((float) 1.95 / 16, (float) 2.8 / 16, 0, 0);
                liftArrowView.setGravity(Gravity.CENTER_VERTICAL);
                liftArrowView.setColor(0xFFFFFFFE);

                final TextView textView = new TextView();
                textView.setId("textView");
                textView.setBasicsAttributes(world, blockEntity.getPos2(), FontList.instance.getFont("Arial"), 6, 0xFFFFFFFF);
                textView.setTextScrolling(true, 6, 0.005F);
                textView.setTextureId("schindler_z_line_3_keypad_1_display");
                textView.setText(TimeStr);
                textView.setWidth(2F / 16);
                textView.setHeight(2F / 16);
                textView.setTextAlign(LiftFloorDisplayView.TextAlign.CENTER);
                textView.setGravity(Gravity.CENTER);


                parentLayout.addChild(liftArrowView);
                parentLayout.addChild(liftFloorDisplayView);
                parentLayout.addChild(textView);
            }
        }
        parentLayout.render();
    }
}