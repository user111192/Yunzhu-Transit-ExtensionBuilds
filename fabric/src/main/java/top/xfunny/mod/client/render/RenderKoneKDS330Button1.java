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
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.block.KoneKDS330Button1;
import top.xfunny.mod.block.base.LiftButtonsBase;
import top.xfunny.mod.client.resource.FontList;
import top.xfunny.mod.client.view.*;
import top.xfunny.mod.client.view.view_group.FrameLayout;
import top.xfunny.mod.client.view.view_group.LinearLayout;
import top.xfunny.mod.item.YteGroupLiftButtonsLinker;
import top.xfunny.mod.item.YteLiftButtonsLinker;
import top.xfunny.mod.util.ReverseRendering;

import java.util.Comparator;

public class RenderKoneKDS330Button1 extends BlockEntityRenderer<KoneKDS330Button1.BlockEntity> implements DirectionHelper, IGui, IBlock {

    private static final int HOVER_COLOR = 0xFFFFFFFF;
    private static final int PRESSED_COLOR = 0xFF00FF00;
    private static final Identifier ARROW_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/kone_m_arrow_1.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/kone_m_button_1.png");
    private static final Identifier LIGHT_TEXTURE = new Identifier(top.xfunny.mod.Init.MOD_ID, "textures/block/kone_m_button_1_light.png");

    public RenderKoneKDS330Button1(Argument dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(KoneKDS330Button1.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
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
            graphicsHolder.translate(0, 0, 7.5F / 16 - SMALL_OFFSET);
        });


        //创建一个纵向的linear layout作为最底层的父容器
        final LinearLayout parentLayout = new LinearLayout(true);
        parentLayout.setBasicsAttributes(world, blockPos);//传入必要的参数
        parentLayout.setStoredMatrixTransformations(storedMatrixTransformations1);
        parentLayout.setParentDimensions((float) 4 / 16, (float) 12 / 16);//宽度为8，高度为16，宽高取决于外呼模型像素大小，一个立方体其中一个面的像素宽高为16x16
        parentLayout.setPosition((float) -0.125, (float) 0.0625);//通过设置坐标的方式设置底层layout的位置
        parentLayout.setWidth(LayoutSize.MATCH_PARENT);//宽度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置
        parentLayout.setHeight(LayoutSize.MATCH_PARENT);//高度为match_parent，即占满父容器，最底层父容器大小已通过setParentDimensions设置

        //创建一个横向的linear layout用于放置显示屏
        final LinearLayout screenLayout = new LinearLayout(false);
        screenLayout.setBasicsAttributes(world, blockPos);//传入必要的参数
        screenLayout.setWidth(LayoutSize.WRAP_CONTENT);
        screenLayout.setHeight(LayoutSize.WRAP_CONTENT);
        screenLayout.setGravity(Gravity.CENTER_HORIZONTAL);//居中
        screenLayout.setMargin(0, (float) 5 / 16, 0, 0);//设置外边距，可选


        //创建一个FrameLayout用于在剩余的空间中放置按钮
        final FrameLayout buttonLayout = new FrameLayout();
        buttonLayout.setBasicsAttributes(world, blockPos);
        buttonLayout.setWidth(LayoutSize.MATCH_PARENT);
        buttonLayout.setHeight(LayoutSize.MATCH_PARENT);
        buttonLayout.setMargin(0, (float) 1.2 / 16, 0, 0);

        //添加按钮
        final LiftButtonView button = new LiftButtonView();
        button.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, false, false);
        button.setLight(light);
        button.setHover(false);
        button.setDefaultColor(0xFFFFFFFF);
        button.setPressedColor(0xFFFFFFFF);//按钮按下时颜色
        button.setHoverColor(0xFFFFFFFF);//准星瞄准时的颜色
        button.setTexture(BUTTON_TEXTURE, true);//按钮贴图
        button.setWidth(0.6F / 16);//按钮宽度
        button.setHeight(0.6F / 16);//按钮高度
        button.setSpacing(0.1F / 16);//两个按钮的间距
        button.setGravity(Gravity.CENTER);//让按钮在父容器（buttonLayout）中居中

        final LiftButtonView buttonLight = new LiftButtonView();
        buttonLight.setBasicsAttributes(world, blockPos, buttonDescriptor, true, false, false, false);
        buttonLight.setLight(light);
        buttonLight.setHover(true);
        buttonLight.setDefaultColor(0xFFFFFFFF);
        buttonLight.setPressedColor(PRESSED_COLOR);
        buttonLight.setHoverColor(HOVER_COLOR);
        buttonLight.setTexture(LIGHT_TEXTURE, true);
        buttonLight.setWidth(0.6F / 16);
        buttonLight.setHeight(0.6F / 16);
        buttonLight.setClientMedian(0.3);
        buttonLight.setSpacing(0.1F / 16);
        buttonLight.setGravity(Gravity.CENTER);

        //添加外呼与楼层轨道的连线
        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world, blockPos);

        // 创建一个对象列表，用于存储排序后的位置和升降机的配对信息
        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        // 遍历每个轨道位置，进行后续处理
        blockEntity.forEachTrackPosition(trackPosition -> {
            //开始渲染外呼与轨道的连线
            line.RenderLine(holdingLinker, trackPosition);

            //判断是否渲染上下按钮
            KoneKDS330Button1.hasButtonsClient(trackPosition, buttonDescriptor, (floorIndex, lift) -> {
                sortedPositionsAndLifts.add(new ObjectObjectImmutablePair<>(trackPosition, lift));
                final ObjectArraySet<LiftDirection> instructionDirections = lift.hasInstruction(floorIndex);
                instructionDirections.forEach(liftDirection -> {
                    switch (liftDirection) {
                        case DOWN:
                            //向下的按钮亮灯
                            buttonLight.setDownButtonLight();
                            break;
                        case UP:
                            //向上的按钮亮灯
                            buttonLight.setUpButtonLight();
                            break;
                    }
                });
            });
        });
        sortedPositionsAndLifts.sort(Comparator.comparingInt(sortedPositionAndLift -> blockPos.getManhattanDistance(new Vector3i(sortedPositionAndLift.left().data))));

        if (!sortedPositionsAndLifts.isEmpty()) {
            // 确定要渲染的电梯数量，这里设置为2个
            final int count = Math.min(2, sortedPositionsAndLifts.size());
            final boolean reverseRendering = count > 1 && ReverseRendering.reverseRendering(facing.rotateYCounterclockwise(), sortedPositionsAndLifts.get(0).left(), sortedPositionsAndLifts.get(1).left());


            for (int i = 0; i < count; i++) {
                //添加外呼显示屏
                final LiftFloorDisplayView liftFloorDisplayView = new LiftFloorDisplayView();
                liftFloorDisplayView.setBasicsAttributes(world,
                        blockPos,
                        sortedPositionsAndLifts.get(i).right(),
                        FontList.instance.getFont("kone-lcd-segment"),//字体
                        5,//字号
                        0xFFFFFFFF);//字体颜色
//                liftFloorDisplayView.setTextScrolling(true, 2, 0.05F);//true开启滚动，开启滚动时的字数条件(>)，滚动速度
                liftFloorDisplayView.setTextureId("kone-lcd-segment");//字体贴图id，不能与其他显示屏的重复
                liftFloorDisplayView.setWidth((float) 1.6 / 16);//显示屏宽度
                liftFloorDisplayView.setHeight((float) 1.7 / 16);//显示屏高度
                liftFloorDisplayView.setMargin(0, (float) 0.43 / 16, (float) 0.45 / 16, 0);
                //liftFloorDisplayView.setGravity(Gravity.CENTER_HORIZONTAL);
                liftFloorDisplayView.setTextAlign(TextView.HorizontalTextAlign.RIGHT);//文字对齐方式，center为居中对齐，left为左对齐，right为右对齐

                //添加箭头
                final LiftArrowView liftArrowView = new LiftArrowView();
                liftArrowView.setBasicsAttributes(world, blockPos, sortedPositionsAndLifts.get(i).right(), LiftArrowView.ArrowType.AUTO);
                liftArrowView.setTexture(ARROW_TEXTURE);
                liftArrowView.setDimension(0.5F / 16);
                liftArrowView.setMargin(0, (float) 1.37 / 16, 0, 0);
                liftArrowView.setGravity(Gravity.CENTER_HORIZONTAL);
                liftArrowView.setQueuedRenderLayer(QueuedRenderLayer.LIGHT_TRANSLUCENT);
                liftArrowView.setColor(0xFFFFFFFF);

                //创建一个linear layout用于组合数字和箭头
                final LinearLayout numberLayout = new LinearLayout(true);
                numberLayout.setBasicsAttributes(world, blockPos);
                numberLayout.setWidth(LayoutSize.WRAP_CONTENT);
                numberLayout.setHeight(LayoutSize.WRAP_CONTENT);
                numberLayout.addChild(liftArrowView);
                numberLayout.addChild(liftFloorDisplayView);
                //将外呼显示屏添加到刚才设定的screenLayout线性布局中
                if (reverseRendering) {
                    screenLayout.addChild(numberLayout);
                    screenLayout.reverseChildren();
                } else {
                    screenLayout.addChild(numberLayout);
                }
            }
        }

        buttonLayout.addChild(button);//将按钮添加到线性布局中进行渲染
        buttonLayout.addChild(buttonLight);
        parentLayout.addChild(screenLayout);//将screenLayout添加到父容器中
        parentLayout.addChild(buttonLayout);//将buttonLayout添加到父容器中

        parentLayout.render();//渲染父容器
    }
}