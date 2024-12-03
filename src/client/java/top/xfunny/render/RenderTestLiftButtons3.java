package top.xfunny.render;

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
import top.xfunny.block.TestLiftButtons;
import top.xfunny.block.TestLiftButtonsWithoutScreen;
import top.xfunny.block.base.LiftButtonsBase;
import top.xfunny.view.*;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;
import top.xfunny.view.view_group.FrameLayout;
import top.xfunny.view.view_group.LinearLayout;
import top.xfunny.util.ReverseRendering;

import java.util.Comparator;


//单位  1F为1贴图像素，左加右减，上加下减
public class RenderTestLiftButtons3 extends BlockEntityRenderer<TestLiftButtons.BlockEntity> implements DirectionHelper, IGui, IBlock {
    public RenderTestLiftButtons3(Argument argument) {
        super(argument);
    }


    @Override
    public void render(TestLiftButtons.BlockEntity blockEntity, float v, GraphicsHolder graphicsHolder1, int light, int overlay) {
        //初始变量
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

        final FrameLayout frameLayout = new FrameLayout();
        frameLayout.setBasicsAttributes(world, blockEntity.getPos2());
        frameLayout.setParentDimensions((float) 16 / 16, (float) 16 / 16);
        frameLayout.setPosition((float) -0.5, (float) 0);
        frameLayout.setWidth(LayoutSize.MATCH_PARENT);
        frameLayout.setHeight(LayoutSize.MATCH_PARENT);
        frameLayout.setGravity(Gravity.END);
        frameLayout.setBackgroundColor(0xFF669999);
        frameLayout.setId("frameLayout");



        //首先添加LinearLayout
        final LinearLayout layout = new LinearLayout(true);
        layout.setBasicsAttributes(world, blockEntity.getPos2());
        layout.setParentDimensions((float) 16 / 16, (float) 16 / 16);
        layout.setPosition((float) -0.5, (float) 0);
        layout.setWidth(LayoutSize.MATCH_PARENT);
        layout.setHeight(LayoutSize.MATCH_PARENT);
        layout.setGravity(Gravity.END);
        layout.setBackgroundColor(0xFF00FF00);
        layout.setId("layout");


        final LinearLayout buttonLayout = new LinearLayout(true);
        buttonLayout.setBasicsAttributes(world, blockEntity.getPos2());
        buttonLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonLayout.setHeight(LayoutSize.WRAP_CONTENT);
        buttonLayout.setMargin((float) 1 / 16, 0, (float)1/16, 0);
        buttonLayout.setGravity(Gravity.START);
        buttonLayout.addStoredMatrixTransformations(graphicsHolder -> {
			graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
		});
        buttonLayout.setBackgroundColor(0xFFFF9966);
        buttonLayout.setId("buttonLayout");

        final LinearLayout buttonLayout2 = new LinearLayout(true);
        buttonLayout2.setBasicsAttributes(world, blockEntity.getPos2());
        buttonLayout2.setWidth(LayoutSize.WRAP_CONTENT);
        buttonLayout2.setHeight(LayoutSize.WRAP_CONTENT);
        buttonLayout2.setGravity(Gravity.CENTER);
        buttonLayout2.addStoredMatrixTransformations(graphicsHolder -> {
			graphicsHolder.translate(0, 0, 0.4475 - SMALL_OFFSET);
		});
        buttonLayout2.setBackgroundColor(0xFF33CCFF);
        buttonLayout2.setId("buttonLayout2");


        //添加按钮控件
        final ButtonView button = new ButtonView();


        //设置按钮基本属性
        button.setDefaultColor(0xFFFFFFFF);
        button.setPressedColor(0xFFD70000);
        button.setHoverColor(0xFFEA7A7A);
        button.setTexture(new Identifier(top.xfunny.Init.MOD_ID, "textures/block/schindler_d_series_line_d2button.png"));
        button.setWidth(3F / 16);
        button.setHeight(3F / 16);
        button.setSpacing(1F / 16);
        button.setLight(light);

        final ButtonView button2 = new ButtonView();
        button2.setDefaultColor(0xFFFFFFFF);
        button2.setPressedColor(0xFFD70000);
        button2.setHoverColor(0xFFEA7A7A);
        button2.setTexture(new Identifier(top.xfunny.Init.MOD_ID, "textures/block/otis_s1_button.png"));
        button2.setWidth(3F / 16);
        button2.setHeight(3F / 16);
        button2.setSpacing(1F / 16);
        button2.setLight(light);




        //设置按钮动态属性
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false,false);
        button.setBasicsAttributes(world, blockEntity.getPos2());
        button.setDescriptor(buttonDescriptor);

        button2.setBasicsAttributes(world, blockEntity.getPos2());
        button2.setDescriptor(buttonDescriptor);







        //添加连线
        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world,blockEntity.getPos2());

        // 创建一个对象列表，用于存储排序后的位置和升降机的配对信息
        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();


        // 遍历每个轨道位置，进行后续处理
        blockEntity.forEachTrackPosition(trackPosition -> {
            //开始渲染外呼与轨道的连线
            line.RenderLine(holdingLinker, trackPosition);

            //判断是否渲染上下按钮
            TestLiftButtonsWithoutScreen.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            //添加向下的按钮
                            button.setDownButtonLight();
                            button2.setDownButtonLight();

                            break;
                        case UP:
                            //添加向上的按钮
                            button.setUpButtonLight();
                            button2.setUpButtonLight();

                            break;
                    }
                });
            });
        });

        //按距离对数组元素进行排序，使其只渲染最近的两部电梯的信息
        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockEntity.getPos2().getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));


        if (!sortedPositionsAndLifts.isEmpty()) {
            // 确定要渲染的电梯数量，这里设置为2个
            final int count = Math.min(2, sortedPositionsAndLifts.size());
            final boolean reverseRendering = count > 1 && ReverseRendering.reverseRendering(facing.rotateYCounterclockwise(), sortedPositionsAndLifts.get(0).left(), sortedPositionsAndLifts.get(1).left());


            for (int i = 0; i < count; i++) {
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
//新建电梯显示屏控件
                if(reverseRendering){
                    //layout2.addChild(liftFloorDisplayView);
                }else{
                    //layout2.addChild(liftFloorDisplayView);
                }

            }
        }

        //将按钮添加到线性布局中进行渲染

        //layout.addChild(layout2);
        buttonLayout.addChild(button);
        buttonLayout2.addChild(button2);

        //layout.addChild(buttonLayout);
        //layout.addChild(buttonLayout2);

        frameLayout.addChild(buttonLayout);
        frameLayout.addChild(buttonLayout2);
        frameLayout.render();

        //渲染线性布局（只需渲染最底层的）
        //layout.render();
    }
}
