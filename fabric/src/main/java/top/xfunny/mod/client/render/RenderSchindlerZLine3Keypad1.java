package top.xfunny.mod.client.render;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.PlayerHelper;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.Init;
import top.xfunny.mod.block.SchindlerZLine3Keypad1;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.keymapping.SchindlerZLine3Keypad1KeyMapping;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.util.ArrayListToString;
import top.xfunny.mod.client.util.TransformPositionX;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;


import java.util.ArrayList;

public class RenderSchindlerZLine3Keypad1 extends BlockEntityRenderer<SchindlerZLine3Keypad1.BlockEntity> implements DirectionHelper, IGui, IBlock {
    private static final int HOVER_COLOR = 0xFFFFFFFF;
    private final boolean isOdd;
    private final float buttonMarginRight = 0.225F / 16;
    private final float buttonHight = 1 * ((float) 101 / 94) / 16;


    public RenderSchindlerZLine3Keypad1(Argument dispatcher, boolean isOdd) {
        super(dispatcher);
        this.isOdd = isOdd;
    }

    public void render(SchindlerZLine3Keypad1.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        final String screenId = blockEntity.getScreenId();

        if (world == null) {
            return;
        }

        final ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().getPlayerMapped();
        if (clientPlayerEntity == null) {
            return;
        }

        //Init.LOGGER.info(String.valueOf(light));

        final boolean holdingLinker = PlayerHelper.isHolding(PlayerEntity.cast(clientPlayerEntity), item -> item.data instanceof YteLiftButtonsLinker || item.data instanceof YteGroupLiftButtonsLinker);
        final BlockPos blockPos = blockEntity.getPos2();
        final BlockState blockState = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        final ArrayList<Object> inputNumber = blockEntity.getInputString();

        final HitResult hitResult = MinecraftClient.getInstance().getCrosshairTargetMapped();
        final Vector3d hitLocation = hitResult.getPos();

        final double hitY = MathHelper.fractionalPart(hitLocation.getYMapped());
        final double hitX = MathHelper.fractionalPart(hitLocation.getXMapped());
        final double hitZ = MathHelper.fractionalPart(hitLocation.getZMapped());

        SchindlerZLine3Keypad1KeyMapping mapping = new SchindlerZLine3Keypad1KeyMapping();
        double transformedX = TransformPositionX.transform(hitX, hitZ, facing);

        String hitButton = mapping.mapping("schindler_z_line_3_keypad_1_key_mapping_input", transformedX, hitY);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(-0.5, 0, 0.003);
        });

        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockEntity.getPos2());
        blockEntity.forEachTrackPosition(trackPosition -> {
            line.RenderLine(holdingLinker, trackPosition);
        });

        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockEntity.getPos2());
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 6 / 16, (float) 16 / 16);
        parentLayout.setPosition((float) 5 / 16, (float) 0);
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);//宽度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);//高度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置

        final LinearLayout group1 = new LinearLayout(false);
        group1.setBasicsAttributes(world, blockEntity.getPos2());
        group1.setWidth(LayoutSize.MATCH_PARENT);
        group1.setHeight(LayoutSize.WRAP_CONTENT);
        group1.setMargin(1.25F / 16, screenId.equals("schindler_z_line_3_keypad_1_key_mapping_input") || screenId.equals("schindler_z_line_3_keypad_1_key_mapping_accessibility") || screenId.equals("schindler_z_line_3_keypad_1_key_mapping_identifier") ? 1.175F / 16 : 5.6F / 16, 0, 0);
        group1.addStoredMatrixTransformations(graphicsHolder -> graphicsHolder.translate(0, 0, -0.013));

        final LinearLayout group2 = new LinearLayout(false);
        group2.setBasicsAttributes(world, blockEntity.getPos2());
        group2.setWidth(LayoutSize.MATCH_PARENT);
        group2.setHeight(LayoutSize.WRAP_CONTENT);
        group2.setMargin(1.25F / 16, 0.625F / 16, 0, 0);

        final LinearLayout group3 = new LinearLayout(false);
        group3.setBasicsAttributes(world, blockEntity.getPos2());
        group3.setWidth(LayoutSize.MATCH_PARENT);
        group3.setHeight(LayoutSize.WRAP_CONTENT);
        group3.setMargin(1.25F / 16, 0.625F / 16, 0, 0);

        final LinearLayout group4 = new LinearLayout(false);
        group4.setBasicsAttributes(world, blockEntity.getPos2());
        group4.setWidth(LayoutSize.MATCH_PARENT);
        group4.setHeight(LayoutSize.WRAP_CONTENT);
        group4.setMargin(1.25F / 16, 0.625F / 16, 0, 0);

        if (screenId.equals("schindler_z_line_3_keypad_1_key_mapping_input") || screenId.equals("schindler_z_line_3_keypad_1_key_mapping_identifier")) {
            final FrameLayout textBackgroundLayout = new FrameLayout();
            textBackgroundLayout.setBasicsAttributes(world, blockEntity.getPos2());
            textBackgroundLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
            textBackgroundLayout.setWidth(LayoutSize.WRAP_CONTENT);
            textBackgroundLayout.setHeight(LayoutSize.WRAP_CONTENT);
            textBackgroundLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            textBackgroundLayout.setMargin(0, (float) 1.85 / 16, 0, 0);

            final ImageView imageView = new ImageView();
            imageView.setBasicsAttributes(world, blockEntity.getPos2());
            imageView.setTexture(new Identifier(Init.MOD_ID, "textures/block/white.png"));
            imageView.setWidth(3.5F / 16);
            imageView.setScale(2.575F / 3.5F);
            imageView.setLight(light, QueuedRenderLayer.LIGHT_TRANSLUCENT);
            imageView.setGravity(Gravity.CENTER);

            final TextView textView = new TextView();
            textView.setId("textView");
            textView.setBasicsAttributes(world, blockEntity.getPos2(), FontList.instance.getFont("Arial"), 6, 0xFF212121);
            textView.setTextScrolling(true, 6, 0.005F);
            textView.setTextureId("schindler_z_line_3_keypad_1_display");
            textView.setText(ArrayListToString.arrayListToString(inputNumber));
            textView.setWidth(2F / 16);
            textView.setHeight(2F / 16);
            textView.setTextAlign(LiftFloorDisplayView.TextAlign.CENTER);
            textView.setGravity(Gravity.CENTER);

            textBackgroundLayout.addChild(imageView);
            textBackgroundLayout.addChild(textView);
            parentLayout.addChild(textBackgroundLayout);

        } else if (screenId.equals("schindler_z_line_3_keypad_1_key_mapping_accessibility")) {
            final FrameLayout textBackgroundLayout = new FrameLayout();
            textBackgroundLayout.setBasicsAttributes(world, blockEntity.getPos2());
            textBackgroundLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
            textBackgroundLayout.setWidth(LayoutSize.WRAP_CONTENT);
            textBackgroundLayout.setHeight(LayoutSize.WRAP_CONTENT);
            textBackgroundLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            textBackgroundLayout.setMargin(0, (float) 1.85 / 16, 0, 0);

            final ImageView imageView = new ImageView();
            imageView.setBasicsAttributes(world, blockEntity.getPos2());
            imageView.setTexture(new Identifier(Init.MOD_ID, "textures/block/white.png"));
            imageView.setWidth(3.5F / 16);
            imageView.setScale(2.575F / 3.5F);
            imageView.setLight(light, QueuedRenderLayer.LIGHT_TRANSLUCENT);
            imageView.setGravity(Gravity.CENTER);

            final ImageView imageAccessibility = new ImageView();
            imageAccessibility.setBasicsAttributes(world, blockEntity.getPos2());
            imageAccessibility.setTexture(new Identifier(Init.MOD_ID, "textures/block/schindler_z_line_keypad/accessibility_icon.png"));
            imageAccessibility.setWidth(1F / 16);
            imageAccessibility.setScale(1);
            imageAccessibility.setLight(light, QueuedRenderLayer.LIGHT_TRANSLUCENT);
            imageAccessibility.setGravity(Gravity.CENTER);

            textBackgroundLayout.addChild(imageView);
            textBackgroundLayout.addChild(imageAccessibility);
            parentLayout.addChild(textBackgroundLayout);
        }

        final ButtonView number1 = new ButtonView();
        number1.setId("number1");
        number1.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        number1.setWidth(1F / 16);
        number1.setHeight(buttonHight);
        number1.setMargin(0, 0, buttonMarginRight, 0);
        number1.setLight(light);
        number1.setDefaultColor(0xFFFFFFFF);
        number1.setHoverColor(HOVER_COLOR);
        number1.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/number1.png"));

        final ButtonView number2 = new ButtonView();
        number2.setId("number2");
        number2.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        number2.setWidth(1F / 16);
        number2.setHeight(buttonHight);
        number2.setMargin(0, 0, buttonMarginRight, 0);
        number2.setLight(light);
        number2.setDefaultColor(0xFFFFFFFF);
        number2.setHoverColor(HOVER_COLOR);
        number2.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/number2.png"));

        final ButtonView number3 = new ButtonView();
        number3.setId("number3");
        number3.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        number3.setWidth(1F / 16);
        number3.setHeight(buttonHight);
        number3.setMargin(0, 0, 0, 0);
        number3.setLight(light);
        number3.setDefaultColor(0xFFFFFFFF);
        number3.setHoverColor(HOVER_COLOR);
        number3.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/number3.png"));

        final ButtonView number4 = new ButtonView();
        number4.setId("number4");
        number4.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        number4.setWidth(1F / 16);
        number4.setHeight(buttonHight);
        number4.setMargin(0, 0, buttonMarginRight, 0);
        number4.setLight(light);
        number4.setDefaultColor(0xFFFFFFFF);
        number4.setHoverColor(HOVER_COLOR);
        number4.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/number4.png"));

        final ButtonView number5 = new ButtonView();
        number5.setId("number5");
        number5.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        number5.setWidth(1F / 16);
        number5.setHeight(buttonHight);
        number5.setMargin(0, 0, buttonMarginRight, 0);
        number5.setLight(light);
        number5.setDefaultColor(0xFFFFFFFF);
        number5.setHoverColor(HOVER_COLOR);
        number5.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/number5.png"));

        final ButtonView number6 = new ButtonView();
        number6.setId("number6");
        number6.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        number6.setWidth(1F / 16);
        number6.setHeight(buttonHight);
        number6.setMargin(0, 0, 0, 0);
        number6.setLight(light);
        number6.setDefaultColor(0xFFFFFFFF);
        number6.setHoverColor(HOVER_COLOR);
        number6.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/number6.png"));

        final ButtonView number7 = new ButtonView();
        number7.setId("number7");
        number7.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        number7.setWidth(1F / 16);
        number7.setHeight(buttonHight);
        number7.setMargin(0, 0, buttonMarginRight, 0);
        number7.setLight(light);
        number7.setDefaultColor(0xFFFFFFFF);
        number7.setHoverColor(HOVER_COLOR);
        number7.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/number7.png"));

        final ButtonView number8 = new ButtonView();
        number8.setId("number8");
        number8.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        number8.setWidth(1F / 16);
        number8.setHeight(buttonHight);
        number8.setMargin(0, 0, buttonMarginRight, 0);
        number8.setLight(light);
        number8.setDefaultColor(0xFFFFFFFF);
        number8.setHoverColor(HOVER_COLOR);
        number8.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/number8.png"));

        final ButtonView number9 = new ButtonView();
        number9.setId("number9");
        number9.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        number9.setWidth(1F / 16);
        number9.setHeight(buttonHight);
        number9.setMargin(0, 0, 0, 0);
        number9.setLight(light);
        number9.setDefaultColor(0xFFFFFFFF);
        number9.setHoverColor(HOVER_COLOR);
        number9.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/number9.png"));

        final ButtonView lobby = new ButtonView();
        lobby.setId("lobby");
        lobby.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        lobby.setWidth(1F / 16);
        lobby.setHeight(buttonHight);
        lobby.setMargin(0, 0, buttonMarginRight, 0);
        lobby.setLight(light);
        lobby.setDefaultColor(0xFFFFFFFF);
        lobby.setHoverColor(HOVER_COLOR);
        lobby.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/lobby.png"));

        final ButtonView number0 = new ButtonView();
        number0.setId("number0");
        number0.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        number0.setWidth(1F / 16);
        number0.setHeight(buttonHight);
        number0.setMargin(0, 0, buttonMarginRight, 0);
        number0.setLight(light);
        number0.setDefaultColor(0xFFFFFFFF);
        number0.setHoverColor(HOVER_COLOR);
        number0.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/number0.png"));

        final ButtonView basement = new ButtonView();
        basement.setId("basement");
        basement.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        basement.setWidth(1F / 16);
        basement.setHeight(buttonHight);
        basement.setMargin(0, 0, 0, 0);
        basement.setLight(light);
        basement.setDefaultColor(0xFFFFFFFF);
        basement.setHoverColor(HOVER_COLOR);
        basement.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/basement.png"));

        final ButtonView accessibility = new ButtonView();
        accessibility.setId("accessibility");
        accessibility.setBasicsAttributes(world, blockEntity.getPos2(), hitButton);
        accessibility.setWidth(3.5F / 16);
        accessibility.setHeight(3.5F * (98F / 326) / 16);
        accessibility.setMargin(1.25F / 16, 0.95F / 16, 0, 0);
        accessibility.setLight(light);
        accessibility.setDefaultColor(0xFFFFFFFF);
        accessibility.setHoverColor(HOVER_COLOR);
        accessibility.setTexture(new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/schindler_z_line_keypad/accessibility.png"));

        group1.addChild(number1);
        group1.addChild(number2);
        group1.addChild(number3);

        group2.addChild(number4);
        group2.addChild(number5);
        group2.addChild(number6);

        group3.addChild(number7);
        group3.addChild(number8);
        group3.addChild(number9);

        group4.addChild(lobby);
        group4.addChild(number0);
        group4.addChild(basement);

        parentLayout.addChild(group1);
        parentLayout.addChild(group2);
        parentLayout.addChild(group3);
        parentLayout.addChild(group4);
        parentLayout.addChild(accessibility);

        parentLayout.render();
    }
}
