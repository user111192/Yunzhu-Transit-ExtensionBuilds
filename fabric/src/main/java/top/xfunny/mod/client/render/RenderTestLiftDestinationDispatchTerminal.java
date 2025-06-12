package top.xfunny.mod.client.render;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.TestLiftDestinationDispatchTerminal;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.ButtonView;
import top.xfunny.mod.client.view.LayoutSize;
import top.xfunny.mod.client.view.LineComponent;
import top.xfunny.mod.client.view.TextView;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.TestLiftDestinationDispatchTerminalKeyMapping;
import top.xfunny.mod.util.ArrayListToString;
import top.xfunny.mod.util.TransformPositionX;

import java.util.ArrayList;

public class RenderTestLiftDestinationDispatchTerminal extends BlockEntityRenderer<TestLiftDestinationDispatchTerminal.BlockEntity> implements DirectionHelper, IGui, IBlock {
    private static final int HOVER_COLOR = 0xFFFFCC66;
    private final boolean isOdd;

    public RenderTestLiftDestinationDispatchTerminal(Argument dispatcher, boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    @Override
    public void render(TestLiftDestinationDispatchTerminal.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        final String screenId = blockEntity.getScreenId();

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
        final ArrayList<Object> inputNumber = blockEntity.getInputNumber();

        final HitResult hitResult = MinecraftClient.getInstance().getCrosshairTargetMapped();
        final Vector3d hitLocation = hitResult.getPos();

        final double hitY = MathHelper.fractionalPart(hitLocation.getYMapped());
        final double hitX = MathHelper.fractionalPart(hitLocation.getXMapped());
        final double hitZ = MathHelper.fractionalPart(hitLocation.getZMapped());

        TestLiftDestinationDispatchTerminalKeyMapping mapping = new TestLiftDestinationDispatchTerminalKeyMapping();
        double transformedX = TransformPositionX.transform(hitX, hitZ, facing);

        String hitButton = mapping.mapping("test_lift_destination_dispatch_terminal_key_mapping_home", transformedX, hitY);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(-0.5, 0, 7.9F / 16 - SMALL_OFFSET);
        });

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);
        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
        });

        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockPos);
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 16 / 16, (float) 11 / 16);
        parentLayout.setPosition((float) 0, (float) 0);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);//宽度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);//高度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置


        if (screenId.equals("test_lift_destination_dispatch_terminal_key_mapping_home")) {
            final LinearLayout group1 = new LinearLayout(false);
            group1.setBasicsAttributes(world, blockPos);
            group1.setWidth(LayoutSize.MATCH_PARENT);
            group1.setHeight(LayoutSize.WRAP_CONTENT);
            group1.setMargin(0, 1F / 16, 0, 0);

            final LinearLayout group2 = new LinearLayout(false);
            group2.setBasicsAttributes(world, blockPos);
            group2.setWidth(LayoutSize.MATCH_PARENT);
            group2.setHeight(LayoutSize.WRAP_CONTENT);
            group2.setMargin(0, 1F / 16, 0, 0);

            final LinearLayout group3 = new LinearLayout(false);
            group3.setBasicsAttributes(world, blockPos);
            group3.setWidth(LayoutSize.MATCH_PARENT);
            group3.setHeight(LayoutSize.WRAP_CONTENT);
            group3.setMargin(0, 1F / 16, 0, 0);

            final TextView textView = new TextView();
            textView.setId("textView");
            textView.setBasicsAttributes(world, blockPos, FontList.instance.getFont("mitsubishi_modern"), 6, HOVER_COLOR);
            textView.setDisplayLength(19, 0.005F);
            textView.setTextureId("test_lift_destination_dispatch_terminal_display");
            textView.setText(ArrayListToString.arrayListToString(inputNumber));
            textView.setWidth(11F / 16);
            textView.setHeight(2F / 16);
            textView.setMargin(1F / 16, 1F / 16, 0, 0);
            textView.setTextAlign(TextView.HorizontalTextAlign.LEFT);

            final ButtonView number1 = new ButtonView();
            number1.setId("number1");
            number1.setBasicsAttributes(world, blockPos, hitButton);
            number1.setWidth(1F / 16);
            number1.setHeight(1F / 16);
            number1.setMargin(1F / 16, 0, 0, 0);
            number1.setLight(light);
            number1.setDefaultColor(0xFFFFFFFF);
            number1.setHoverColor(HOVER_COLOR);
            number1.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/number1.png"));

            final ButtonView number2 = new ButtonView();
            number2.setId("number2");
            number2.setBasicsAttributes(world, blockPos, hitButton);
            number2.setWidth(1F / 16);
            number2.setHeight(1F / 16);
            number2.setMargin(1F / 16, 0, 0, 0);
            number2.setLight(light);
            number2.setDefaultColor(0xFFFFFFFF);
            number2.setHoverColor(HOVER_COLOR);
            number2.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/number2.png"));

            final ButtonView number3 = new ButtonView();
            number3.setId("number3");
            number3.setBasicsAttributes(world, blockPos, hitButton);
            number3.setWidth(1F / 16);
            number3.setHeight(1F / 16);
            number3.setMargin(1F / 16, 0, 0, 0);
            number3.setLight(light);
            number3.setDefaultColor(0xFFFFFFFF);
            number3.setHoverColor(HOVER_COLOR);
            number3.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/number3.png"));

            final ButtonView number4 = new ButtonView();
            number4.setId("number4");
            number4.setBasicsAttributes(world, blockPos, hitButton);
            number4.setWidth(1F / 16);
            number4.setHeight(1F / 16);
            number4.setMargin(1F / 16, 0, 0, 0);
            number4.setLight(light);
            number4.setDefaultColor(0xFFFFFFFF);
            number4.setHoverColor(HOVER_COLOR);
            number4.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/number4.png"));

            final ButtonView number5 = new ButtonView();
            number5.setId("number5");
            number5.setBasicsAttributes(world, blockPos, hitButton);
            number5.setWidth(1F / 16);
            number5.setHeight(1F / 16);
            number5.setMargin(1F / 16, 0, 0, 0);
            number5.setLight(light);
            number5.setDefaultColor(0xFFFFFFFF);
            number5.setHoverColor(HOVER_COLOR);
            number5.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/number5.png"));

            final ButtonView number6 = new ButtonView();
            number6.setId("number6");
            number6.setBasicsAttributes(world, blockPos, hitButton);
            number6.setWidth(1F / 16);
            number6.setHeight(1F / 16);
            number6.setMargin(1F / 16, 0, 0, 0);
            number6.setLight(light);
            number6.setDefaultColor(0xFFFFFFFF);
            number6.setHoverColor(HOVER_COLOR);
            number6.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/number6.png"));

            final ButtonView number7 = new ButtonView();
            number7.setId("number7");
            number7.setBasicsAttributes(world, blockPos, hitButton);
            number7.setWidth(1F / 16);
            number7.setHeight(1F / 16);
            number7.setMargin(1F / 16, 0, 0, 0);
            number7.setLight(light);
            number7.setDefaultColor(0xFFFFFFFF);
            number7.setHoverColor(HOVER_COLOR);
            number7.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/number7.png"));

            final ButtonView number8 = new ButtonView();
            number8.setId("number8");
            number8.setBasicsAttributes(world, blockPos, hitButton);
            number8.setWidth(1F / 16);
            number8.setHeight(1F / 16);
            number8.setMargin(1F / 16, 0, 0, 0);
            number8.setLight(light);
            number8.setDefaultColor(0xFFFFFFFF);
            number8.setHoverColor(HOVER_COLOR);
            number8.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/number8.png"));

            final ButtonView number9 = new ButtonView();
            number9.setId("number9");
            number9.setBasicsAttributes(world, blockPos, hitButton);
            number9.setWidth(1F / 16);
            number9.setHeight(1F / 16);
            number9.setMargin(1F / 16, 0, 0, 0);
            number9.setLight(light);
            number9.setDefaultColor(0xFFFFFFFF);
            number9.setHoverColor(HOVER_COLOR);
            number9.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/number9.png"));

            final ButtonView number0 = new ButtonView();
            number0.setId("number0");
            number0.setBasicsAttributes(world, blockPos, hitButton);
            number0.setWidth(1F / 16);
            number0.setHeight(1F / 16);
            number0.setMargin(1F / 16, 0, 0, 0);
            number0.setLight(light);
            number0.setDefaultColor(0xFFFFFFFF);
            number0.setHoverColor(HOVER_COLOR);
            number0.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/number0.png"));

            final ButtonView clearNumber = new ButtonView();
            clearNumber.setId("clearNumber");
            clearNumber.setBasicsAttributes(world, blockPos, hitButton);
            clearNumber.setWidth(1F / 16);
            clearNumber.setHeight(1F / 16);
            clearNumber.setMargin(1F / 16, 0, 0, 0);
            clearNumber.setLight(light);
            clearNumber.setDefaultColor(0xFFFFFFFF);
            clearNumber.setHoverColor(HOVER_COLOR);
            clearNumber.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/cross.png"));

            final ButtonView callLift = new ButtonView();
            callLift.setId("callLift");
            callLift.setBasicsAttributes(world, blockPos, hitButton);
            callLift.setWidth(1F / 16);
            callLift.setHeight(1F / 16);
            callLift.setMargin(1F / 16, 0, 0, 0);
            callLift.setLight(light);
            callLift.setDefaultColor(0xFFFFFFFF);
            callLift.setHoverColor(HOVER_COLOR);
            callLift.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/call_lift.png"));

            group1.addChild(number1);
            group1.addChild(number2);
            group1.addChild(number3);
            group1.addChild(clearNumber);

            group2.addChild(number4);
            group2.addChild(number5);
            group2.addChild(number6);
            group2.addChild(number0);

            group3.addChild(number7);
            group3.addChild(number8);
            group3.addChild(number9);
            group3.addChild(callLift);

            parentLayout.addChild(textView);
            parentLayout.addChild(group1);
            parentLayout.addChild(group2);
            parentLayout.addChild(group3);
        }


        parentLayout.render();

    }
}
