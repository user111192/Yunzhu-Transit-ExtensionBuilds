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
import top.xfunny.mod.block.OtisSeries1Screen;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

import java.util.Comparator;

public class RenderOtisSeries1Screen extends BlockEntityRenderer<OtisSeries1Screen.BlockEntity> implements DirectionHelper, IGui, IBlock {
    private final boolean isOdd;

    public RenderOtisSeries1Screen(Argument dispatcher, Boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    @Override
    public void render(OtisSeries1Screen.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
            graphicsHolder.translate(0, 0, 7F / 16 - SMALL_OFFSET);
        });

        FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(4.8F / 16, 6.5F / 16);
        parentLayout.setPosition(-2.4F / 16, 0.75F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        LinearLayout backgroundLayout = new LinearLayout(false);
        backgroundLayout.setBasicsAttributes(world, blockPos);
        backgroundLayout.setWidth(LayoutSize.WRAP_CONTENT);
        backgroundLayout.setHeight(LayoutSize.WRAP_CONTENT);
        backgroundLayout.setGravity(Gravity.CENTER);
        backgroundLayout.setBackgroundColor(0xFF000000);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
            OtisSeries1Screen.LiftCheck(trackPosition, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
            });
        });

        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            final int count = 1;

            for (int i = 0; i < count; i++) {
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("otis_series1"),
                        4,
                        0xFF1D953F);
                liftFloorDisplayView.setTextureId("otis_series1_screen");
                liftFloorDisplayView.setWidth(2.8F / 16);
                liftFloorDisplayView.setHeight(4F / 16);
                liftFloorDisplayView.setMargin(0, 0, 0.8F / 16, 0);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);
                liftFloorDisplayView.setDisplayLength(2, 0);

                backgroundLayout.addChild(liftFloorDisplayView);
            }
        }
        parentLayout.addChild(backgroundLayout);
        parentLayout.render();
    }
}