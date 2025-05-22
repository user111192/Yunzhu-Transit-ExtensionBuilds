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
import top.xfunny.mod.block.OtisSeries3Button1;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

public class RenderOtisSeries3Button1 extends BlockEntityRenderer<OtisSeries3Button1.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private final int HOVER_COLOR = 0xFFFF9999;
    private final int PRESSED_COLOR = 0xFFBB0000;
    private final Identifier BUTTON_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/otis_s3_button_1.png");
    private final Identifier ARROW_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/otis_s3_button_1_arrow.png");
    private final Identifier BUTTON_LIGHT_TEXTURE = new Identifier(Init.MOD_ID, "textures/block/otis_s3_button_1_light.png");
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
            graphicsHolder.translate(0, 0, 7.65F/16 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 1.35 / 16, (float) 8 / 16);
        parentLayout.setPosition((float) -0.0405, (float) -0.235);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);
        parentLayout.setMargin(0, 0, (float) 0.1 / 16, (float) 0.1 / 16);

        LiftButtonView button = new LiftButtonView();
        button.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, false, true);
        button.setLight(light);
        button.setDefaultColor(0xFFFFFFFF);
        button.setHover(true);
        button.setPressedColor(0xFFFFFFFF);
        button.setHoverColor(0xFFFFFFFF);
        button.setTexture(BUTTON_TEXTURE, false);
        button.setWidth(0.6F / 16);
        button.setHeight(0.6F / 16);
        button.setSpacing(0.35F / 16);
        button.setGravity(Gravity.END);

        LiftButtonView buttonLight = new LiftButtonView();
        buttonLight.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, false, true);
        buttonLight.setLight(light);
        buttonLight.setDefaultColor(0xFF111111);
        buttonLight.setHover(true);
        buttonLight.setPressedColor(PRESSED_COLOR);
        buttonLight.setHoverColor(HOVER_COLOR);
        buttonLight.setTexture(new Identifier(Init.MOD_ID, "textures/block/otis_s3_button_1_light.png"), false);
        buttonLight.setWidth(0.6F / 16);
        buttonLight.setHeight(0.6F / 16);
        buttonLight.setSpacing(0.35F / 16);
        buttonLight.setGravity(Gravity.END);

        LiftButtonView buttonArrow = new LiftButtonView();
        buttonArrow.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, false, true);
        buttonArrow.setLight(light);
        buttonArrow.setDefaultColor(0xFFFFFFFF);
        buttonArrow.setHover(true);
        buttonArrow.setPressedColor(0xFFFFFFFF);
        buttonArrow.setHoverColor(0xFFFFFFFF);
        buttonArrow.setTexture(ARROW_TEXTURE, true);
        buttonArrow.setWidth(0.6F / 16);
        buttonArrow.setHeight(0.6F / 16);
        buttonArrow.setSpacing(0.35F / 16);
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
        parentLayout.addChild(buttonArrow);

        parentLayout.render();
    }
}
