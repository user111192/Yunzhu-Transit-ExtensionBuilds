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
import top.xfunny.mod.block.OtisSeries1Button;
import top.xfunny.mod.block.OtisSeries3Button1;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.Gravity;
import top.xfunny.mod.client.view.LayoutSize;
import top.xfunny.mod.client.view.LiftButtonView;
import top.xfunny.mod.client.view.LineComponent;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

public class RenderOtisSeries3Button1 extends BlockEntityRenderer<OtisSeries3Button1.BlockEntity> implements DirectionHelper, IGui, IBlock {
    public RenderOtisSeries3Button1(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(OtisSeries3Button1.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
        });

        FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 4.8 / 16, (float) 6.5 / 16);
        parentLayout.setPosition((float) -2.4 / 16, (float) 0.75 / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        FrameLayout backgroundLayout = new FrameLayout();
        backgroundLayout.setBasicsAttributes(world, blockEntity.getPos2());
        backgroundLayout.setWidth(LayoutSize.WRAP_CONTENT);
        backgroundLayout.setHeight(LayoutSize.WRAP_CONTENT);
        backgroundLayout.setGravity(Gravity.CENTER);
        backgroundLayout.addStoredMatrixTransformations(graphicsHolder -> {
            graphicsHolder.translate(0, 0, -3 * SMALL_OFFSET);
        });

        LinearLayout buttonLayout = new LinearLayout(false);
        buttonLayout.setBasicsAttributes(world, blockEntity.getPos2());
        buttonLayout.setWidth(LayoutSize.WRAP_CONTENT);
        buttonLayout.setHeight(LayoutSize.WRAP_CONTENT);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setMargin(0, 0, (float) 0.4 / 16, (float) 0.4 / 16);

        LiftButtonView button = new LiftButtonView();
        button.setBasicsAttributes(world, blockEntity.getPos2(), buttonDescriptor, true, false, false, false);
        button.setLight(light);
        button.setHover(true);
        button.setDefaultColor(0xFFFFFFFF);
        button.setPressedColor(0xFFFFFFFF);
        button.setHoverColor(0xFFFFFFFF);
        button.setTexture(new Identifier(Init.MOD_ID, "textures/block/otis_s3_button_1.png"), true);
        button.setWidth(1F / 16);
        button.setHeight(1F / 16);
        button.setSpacing(0.5F / 16);
        button.setGravity(Gravity.END);

        LiftButtonView buttonLight = new LiftButtonView();
        button.setBasicsAttributes(world, blockEntity.getPos2(), buttonDescriptor, true, false, false, false);
        button.setLight(light);
        button.setHover(true);
        button.setDefaultColor(0xFF343434);
        button.setPressedColor(0xFFFF0000);
        button.setHoverColor(0xFF990000);
        button.setTexture(new Identifier(Init.MOD_ID, "textures/block/otis_s3_button_1_light.png"), true);
        button.setWidth(1F / 16);
        button.setHeight(1F / 16);
        button.setSpacing(0.5F / 16);
        button.setGravity(Gravity.END);

        LiftButtonView buttonArrow = new LiftButtonView();
        buttonArrow.setBasicsAttributes(world, blockEntity.getPos2(), buttonDescriptor, true, false, false, false);
        buttonArrow.setLight(light);
        buttonArrow.setHover(false);
        buttonArrow.setDefaultColor(0xFFFFFFFF);
        buttonArrow.setPressedColor(0xFFFFFFFF);
        buttonArrow.setHoverColor(0xFFFFFFFF);
        buttonArrow.setTexture(new Identifier(Init.MOD_ID, "textures/block/otis_s3_button_1_arrow.png"), true);
        buttonArrow.setWidth(1F / 16);
        buttonArrow.setHeight(1F / 16);
        buttonArrow.setSpacing(0.5F / 16);
        buttonArrow.setMargin(0, 0, (float) 0.4 / 16, 0);
        buttonArrow.setGravity(Gravity.START);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            OtisSeries3Button1.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            button.setDownButtonLight();
                            break;
                        case UP:
                            button.setUpButtonLight();
                            break;
                    }
                });
            });

        });
        buttonLayout.addChild(buttonArrow);
        buttonLayout.addChild(button);
        buttonLayout.addChild(buttonLight);
        parentLayout.addChild(backgroundLayout);
        backgroundLayout.addChild(buttonLayout);
        parentLayout.render();
    }
}