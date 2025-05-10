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
import top.xfunny.mod.block.CESScreen1Even;
import top.xfunny.mod.block.TonicDSScreen1Even;
import top.xfunny.mod.block.base.LiftPanelBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import java.util.Comparator;

public class RenderTonicDSScreen1<T extends LiftPanelBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;

    public RenderTonicDSScreen1(Argument dispatcher, Boolean isOdd) {
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
            graphicsHolder.translate(0, 0, 0.062 - SMALL_OFFSET);
        });

        final LinearLayout parentLayout = new LinearLayout(false);
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 7.5 / 16, (float) 5 / 16);
        parentLayout.setPosition(isOdd ? -0.284375F : -0.784375F, 0.5725F);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);


        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
            TonicDSScreen1Even.LiftCheck(trackPosition, (floorIndex, lift) -> {
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
                        FontList.instance.getFont("acmeled"),
                        8,
                        0xFFFF0000);
                liftFloorDisplayView.setTextureId("tonic_ds_screen_1");
                liftFloorDisplayView.setWidth((float) 2.6 / 16);
                liftFloorDisplayView.setHeight((float) 2.8 / 16);
                liftFloorDisplayView.setGravity(Gravity.CENTER_VERTICAL);
                liftFloorDisplayView.setTextAlign(LiftFloorDisplayView.TextAlign.RIGHT);
                liftFloorDisplayView.setLetterSpacing(0);
                liftFloorDisplayView.setTextScrolling(true, 5, 0.01F);
                liftFloorDisplayView.setMargin((float) 1.5 / 16, 0, 0, 0);
                liftFloorDisplayView.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -SMALL_OFFSET));

                final LiftArrowView liftArrowView_right = new LiftArrowView();
                liftArrowView_right.setBasicsAttributes(world, blockEntity.getPos2(), sortedPositionsAndLifts.get(i).right());
                liftArrowView_right.setTexture(new Identifier(Init.MOD_ID, "textures/block/dewhurst_ul200_arrow_1.png"));
                liftArrowView_right.setArrowScrolling(false, 0.05F);
                liftArrowView_right.setWidth((float) 0.625 / 16);
                liftArrowView_right.setHeight((float) 0.625 / 16);
                liftArrowView_right.setMargin((float) 1.125 / 16, (float) 3 / 16, 0, 0);
                liftArrowView_right.setGravity(Gravity.CENTER_VERTICAL);
                liftArrowView_right.setColor(0xFFFF0000);

                final LiftArrowView liftArrowView_left = new LiftArrowView();
                liftArrowView_left.setBasicsAttributes(world, blockEntity.getPos2(), sortedPositionsAndLifts.get(i).right());
                liftArrowView_left.setTexture(new Identifier(Init.MOD_ID, "textures/block/dewhurst_ul200_arrow_1.png"));
                liftArrowView_left.setArrowScrolling(false, 0.05F);
                liftArrowView_left.setWidth((float) 0.625 / 16);
                liftArrowView_left.setHeight((float) 0.625 / 16);
                liftArrowView_left.setMargin((float) -5.75 / 16, (float) 3 / 16, 0, 0);
                liftArrowView_left.setGravity(Gravity.CENTER_VERTICAL);
                liftArrowView_left.setColor(0xFFFF0000);


                parentLayout.addChild(liftFloorDisplayView);
                parentLayout.addChild(liftArrowView_right);
                parentLayout.addChild(liftArrowView_left);
            }
        }
        parentLayout.render();
    }
}