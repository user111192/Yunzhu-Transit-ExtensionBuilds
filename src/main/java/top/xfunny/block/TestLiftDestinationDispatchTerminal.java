package top.xfunny.block;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.BlockEntityTypes;
import top.xfunny.Init;
import top.xfunny.Items;
import top.xfunny.block.base.LiftDestinationDispatchTerminalBase;
import top.xfunny.keymapping.TestLiftDestinationDispatchTerminalKeyMapping;
import top.xfunny.util.ArrayListToString;
import top.xfunny.util.TransformPositionX;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestLiftDestinationDispatchTerminal extends LiftDestinationDispatchTerminalBase {
    public String screenId = "test_lift_destination_dispatch_terminal_key_mapping_home";

    public TestLiftDestinationDispatchTerminal() {
        super(true);
    }


    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 11, 1, IBlock.getStatePropertySafe(state, FACING));
    }


    public static void hasButtonsClient(BlockPos trackPosition, FloorLiftCallback callback){
        LiftDestinationDispatchTerminalBase.hasButtonsClient(trackPosition, callback);//todo：需要注意
    }

    /**
     * 创建方块实体扩展
     * 此方法用于实例化与电梯按钮相关的方块实体
     *
     * @param blockPos   方块的位置
     * @param blockState 方块的状态
     * @return 返回一个新的 {@code BlockEntityExtension} 实例，代表电梯按钮的方块实体
     */
    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TestLiftDestinationDispatchTerminal.BlockEntity(blockPos, blockState);
    }

    /**
     * 添加块属性
     * 此方法用于向块的属性列表中添加方向和解锁状态属性
     *
     * @param properties 块的属性列表，包含所有与块相关的属性
     */
    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        // 添加块的方向属性
        properties.add(FACING);
        // 添加块的解锁状态属性
        properties.add(UNLOCKED);

    }

    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final double hitX = MathHelper.fractionalPart(hit.getPos().getXMapped());
        final double hitY = MathHelper.fractionalPart(hit.getPos().getYMapped());
        final double hitZ = MathHelper.fractionalPart(hit.getPos().getZMapped());


        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        double transformedX = TransformPositionX.transform(hitX, hitZ, facing);
        final org.mtr.mapping.holder.BlockEntity blockEntity = world.getBlockEntity(pos);
        final LiftDestinationDispatchTerminalBase.BlockEntityBase data = (LiftDestinationDispatchTerminalBase.BlockEntityBase) blockEntity.data;
        final TestLiftDestinationDispatchTerminal.BlockEntity data1 = (TestLiftDestinationDispatchTerminal.BlockEntity) blockEntity.data;

        TestLiftDestinationDispatchTerminalKeyMapping mapping = new TestLiftDestinationDispatchTerminalKeyMapping();


        if (player.isHolding(top.xfunny.Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(top.xfunny.Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get()) || player.isHolding(top.xfunny.Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER.get())) {
            Init.LOGGER.info("onUse2");
            return ActionResult.PASS;
        } else{

            //todo:以后区分不同id下的点击事件
            String testOutput = mapping.mapping(screenId, transformedX, hitY);
            if(screenId.equals("test_lift_destination_dispatch_terminal_key_mapping_home")){//todo:可能没有必要进行判断
                switch (testOutput){
                    case "number1":
                        data1.addInputNumber(1);
                        break;
                    case "number2":
                        data1.addInputNumber(2);
                        break;
                    case "number3":
                        data1.addInputNumber(3);
                        break;
                    case "number4":
                        data1.addInputNumber(4);
                        break;
                    case "number5":
                        data1.addInputNumber(5);
                        break;
                    case "number6":
                        data1.addInputNumber(6);
                        break;
                    case "number7":
                        data1.addInputNumber(7);
                        break;
                    case "number8":
                        data1.addInputNumber(8);
                        break;
                    case "number9":
                        data1.addInputNumber(9);
                        break;
                    case "number0":
                        data1.addInputNumber(0);
                        break;
                    case "clearNumber":
                        data1.clearInputNumber();
                        break;
                    case "callLift":
                        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                        Init.LOGGER.info("已呼梯");
                        data.callLift(world, pos, data1.processInputNumber());
                        data1.clearInputNumber();
                        data1.addInputNumber("To Lift:"+data.getLiftIdentifier());
                        scheduler.schedule(() -> {
                            data1.clearInputNumber();
                            data1.addInputNumber("Please input floor number!");
                        }, 1, TimeUnit.SECONDS);
                        scheduler.shutdown();
                }
                player.sendMessage(Text.of(("transformedX:"+transformedX+",hity:"+hitY+",您点击了" + testOutput)), true);
            }
        }



        return ActionResult.SUCCESS;
    }

    /**
     * 表示一个可追踪位置的方块实体，扩展自BlockEntityExtension
     * 主要功能是通过CompoundTag来读取和写入特定位置集合
     */
    public static class BlockEntity extends LiftDestinationDispatchTerminalBase.BlockEntityBase {
        public ArrayList<Object> inputNumber = new ArrayList<>();
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.TEST_LIFT_DESTINATION_DISPATCH_TERMINAL.get(), pos, state);
            super.registerScreenId("test_lift_destination_dispatch_terminal_key_mapping_home");//初始化screen
            inputNumber.add("Please input floor number!");
        }
        public void addInputNumber(Object number) {
            if (ArrayListToString.arrayListToString(inputNumber).equals("Please input floor number!")){
                clearInputNumber();
            }
            inputNumber.add(number);
        }

        public void clearInputNumber() {
            inputNumber.clear();
        }

        public ArrayList<Object> getInputNumber() {
            if (inputNumber.isEmpty()){
                inputNumber.add("Please input floor number!");
            }
                return inputNumber;
        }

        public int processInputNumber() {
            // 检查是否所有元素都是数字
            boolean allNumbers = true;
            for (Object obj : inputNumber) {
                if (!(obj instanceof Integer)) {
                    allNumbers = false;
                    break;
                }
            }

            if (allNumbers) {
                // 组合数字
                StringBuilder numberBuilder = new StringBuilder();
                for (Object obj : inputNumber) {
                    numberBuilder.append(obj.toString());
                }

                // 将组合后的字符串转换为整数
                try {
                    int combinedNumber = Integer.parseInt(numberBuilder.toString());
                    System.out.println("组合后的数字: " + combinedNumber);
                    return combinedNumber;
                } catch (NumberFormatException e) {
                    System.out.println("组合后的数字超出整数范围");
                }
            } else {
                System.out.println("输入包含非数字元素");
            }
            return 0;//todo
        }
    }
}
