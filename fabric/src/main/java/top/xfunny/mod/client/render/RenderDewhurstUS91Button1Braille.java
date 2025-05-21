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
import top.xfunny.mod.block.DewhurstUS91Button1Braille;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.view.Gravity;
import top.xfunny.mod.client.view.LiftButtonView;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;

public class RenderDewhurstUS91Button1Braille extends BlockEntityRenderer<DewhurstUS91Button1Braille.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private final int HOVER_COLOR = 0xFFFF9999;
    private final int PRESSED_COLOR = 0xFFFF0000;
    private final Identifier BUTTON_TEXTURE_UP = new Identifier(Init.MOD_ID, "textures/block/dewhurst_us91_button_1_braille_up.png");
    private final Identifier BUTTON_TEXTURE_DOWN = new Identifier(Init.MOD_ID, "textures/block/dewhurst_us91_button_1_braille_down.png");
    private final Identifier BUTTON_LIGHT_TEXTURE_UP = new Identifier(Init.MOD_ID, "textures/block/dewhurst_us91_button_1_braille_up_light.png");
    private final Identifier BUTTON_LIGHT_TEXTURE_DOWN = new Identifier(Init.MOD_ID, "textures/block/dewhurst_us91_button_1_braille_down_light.png");

    public RenderDewhurstUS91Button1Braille(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(DewhurstUS91Button1Braille.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
            graphicsHolder.translate(0, -0.115, 0.062 - SMALL_OFFSET);
        });

        final FrameLayout parentLayout = new FrameLayout();
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 6 / 16, (float) 10 / 16);
        parentLayout.setPosition((float) -0.1875, (float) 0);
        parentLayout.setWidth(top.xfunny.mod.client.view.LayoutSize.MATCH_PARENT);
        parentLayout.setHeight(top.xfunny.mod.client.view.LayoutSize.MATCH_PARENT);

        LiftButtonView button = new LiftButtonView();
        button.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, false, false);
        button.setLight(light);
        button.setDefaultColor(0xFFFFFFFF);
        button.setHover(false);
        button.setPressedColor(0xFFFFFFFF);
        button.setHoverColor(0xFFFFFFFF);
        button.setTexture(BUTTON_TEXTURE_UP, BUTTON_TEXTURE_DOWN, false);
        button.setWidth(1.1F / 16);
        button.setHeight(1.1F / 16);
        button.setSpacing(0.5F / 16);
        button.setGravity(Gravity.CENTER);

        LiftButtonView buttonLight = new LiftButtonView();
        buttonLight.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, false, false);
        buttonLight.setLight(light);
        buttonLight.setDefaultColor(0xFF000000);
        buttonLight.setHover(true);
        buttonLight.setPressedColor(PRESSED_COLOR);
        buttonLight.setHoverColor(HOVER_COLOR);
        buttonLight.setTexture(BUTTON_LIGHT_TEXTURE_UP, BUTTON_LIGHT_TEXTURE_DOWN, false);
        buttonLight.setWidth(1.1F / 16);
        buttonLight.setHeight(1.1F / 16);
        buttonLight.setSpacing(0.5F / 16);
        buttonLight.setGravity(Gravity.CENTER);

        final top.xfunny.mod.client.view.LineComponent line = new top.xfunny.mod.client.view.LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());

        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);

            DewhurstUS91Button1Braille.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
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

        parentLayout.render();
    }
}
