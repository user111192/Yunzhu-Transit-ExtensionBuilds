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
import top.xfunny.mod.block.KoneKDS330Lantern1;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.Gravity;
import top.xfunny.mod.client.view.LayoutSize;
import top.xfunny.mod.client.view.LiftButtonView;
import top.xfunny.mod.client.view.LineComponent;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

public class RenderKoneKDS330Lantern1<T extends LiftButtonsBase.BlockEntityBase> extends BlockEntityRenderer<T> implements DirectionHelper, IGui, IBlock {
    private static final int PRESSED_COLOR = 0xFFFFCC00;
    private static final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/kone_arrow_back.png");
    private static final Identifier BUTTON_LIGHT_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/kone_arrow_light.png");

    public RenderKoneKDS330Lantern1(Argument dispatcher) {
        super(dispatcher);
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
            graphicsHolder.translate(0, 0.17, 7.5F / 16 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions(7.5F / 16, 7.5F / 16);
        parentLayout.setPosition(-3.75F / 16, 4.25F / 16);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);

        LiftButtonView button = new LiftButtonView();
        button.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, true, true);
        button.setLight(255);
        button.setHover(false);
        button.setDefaultColor(0xFFFFFFFF);
        button.setPressedColor(0xFFFFFFFF);
        button.setHoverColor(0xFFFFFFFF);
        button.setTexture(BUTTON_TEXTURE, true);
        button.setWidth(1.4875F / 16);
        button.setHeight(1.75F / 16);
        button.setSpacing(1.235F / 16);
        button.setGravity(Gravity.CENTER);

        LiftButtonView buttonLight = new LiftButtonView();
        buttonLight.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, true, true);
        buttonLight.setLight(255);
        buttonLight.setHover(false);
        buttonLight.setDefaultColor(0x00FFFFFF);
        buttonLight.setPressedColor(0xFFFFFFFF);
        buttonLight.setHoverColor(0x00FFFFFF);
        buttonLight.setTexture(BUTTON_LIGHT_TEXTURE, true);
        buttonLight.setWidth(1.4875F / 16);
        buttonLight.setHeight(1.75F / 16);
        buttonLight.setSpacing(1.235F / 16);
        buttonLight.setGravity(Gravity.CENTER);

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        final LineComponent buttonLine = new LineComponent();
        buttonLine.setBasicsAttributes(world, blockPos);

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            KoneKDS330Lantern1.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {

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

        blockEntity.forEachLiftButtonPosition(buttonPosition -> {
            buttonLine.RenderLine(holdingLinker, buttonPosition, true);
        });
        parentLayout.addChild(button);
        parentLayout.addChild(buttonLight);

        parentLayout.render();
    }
}
