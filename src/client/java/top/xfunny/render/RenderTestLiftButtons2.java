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
import org.mtr.mod.Init;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import top.xfunny.block.TestLiftButtonsWithoutScreen;
import top.xfunny.block.base.LiftButtonsBase;
import top.xfunny.component.ButtonComponent;
import top.xfunny.component.LineComponent;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;
import top.xfunny.layout.LinearLayout;


//单位  1F为1贴图像素，左加右减，上加下减
public class RenderTestLiftButtons2  extends BlockEntityRenderer<TestLiftButtonsWithoutScreen.BlockEntity> implements DirectionHelper, IGui, IBlock {
    public RenderTestLiftButtons2(Argument argument) {
        super(argument);
    }

    @Override
    public void render(TestLiftButtonsWithoutScreen.BlockEntity blockEntity, float v, GraphicsHolder graphicsHolder1, int light, int overlay) {
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


        //首先添加LinearLayout
        final LinearLayout layout = new LinearLayout(true);
        layout.setBasicsAttributes(world, blockEntity.getPos2());
        layout.setPosition(0, 0, 0);


        //添加按钮控件
        final ButtonComponent button = new ButtonComponent();


        //设置按钮基本属性
        button.setDefaultColor(0xFFFFFFFF);
        button.setPressedColor(0xFFD70000);
        button.setHoverColor(0xFFEA7A7A);
        button.setTexture(new Identifier(top.xfunny.Init.MOD_ID, "textures/block/schindler_d_series_line_d2button.png"));
        button.setWidth(3F);
        button.setHeight(3F);
        button.setSpacing(4F);
        button.setLight(light);



        //设置按钮动态属性
        LiftButtonsBase.LiftButtonDescriptor buttonDescriptor = new LiftButtonsBase.LiftButtonDescriptor(false,false);
        button.setBasicsAttributes(world, blockEntity.getPos2());
        button.setDescriptor(buttonDescriptor);




        //添加连线
        final LineComponent line = new LineComponent();
        line.setBasicsAttributes(world,blockEntity.getPos2());

        // 创建一个对象列表，用于存储排序后的位置和升降机的配对信息
        final ObjectArrayList<ObjectObjectImmutablePair<BlockPos, Lift>> sortedPositionsAndLifts = new ObjectArrayList<>();

        // 遍历每个轨道位置，进行后续处理
        blockEntity.forEachTrackPosition(trackPosition -> {
            //开始渲染连线
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
                            break;
                        case UP:
                            //添加向上的按钮
                            button.setUpButtonLight();
                            break;
                    }
                });
            });
        });

        //将按钮添加到线性布局中进行渲染，倒序添加！
        layout.addChild(button);

        //渲染线性布局（只需渲染最底层的）
        layout.render();
    }
}
