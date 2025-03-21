package top.xfunny.block;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.IBlock;
import top.xfunny.BlockEntityTypes;
import top.xfunny.Init;
import top.xfunny.Items;
import top.xfunny.block.base.LiftDestinationDispatchTerminalBase;
import top.xfunny.keymapping.SchindlerZLine3Keypad1KeyMapping;
import top.xfunny.util.ArrayListToString;
import top.xfunny.util.TransformPositionX;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class SchindlerZLine3Keypad1 extends LiftDestinationDispatchTerminalBase {

    public SchindlerZLine3Keypad1() {
        super(true);
    }

    public static void hasButtonsClient(BlockPos trackPosition, FloorLiftCallback callback) {
        LiftDestinationDispatchTerminalBase.hasButtonsClient(trackPosition, callback);
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IBlock.getVoxelShapeByDirection(5, 0, 0, 11, 16, 1.2, IBlock.getStatePropertySafe(state, FACING));
    }

    @Nonnull
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SchindlerZLine3Keypad1.BlockEntity(blockPos, blockState);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
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
        if (!(blockEntity.data instanceof LiftDestinationDispatchTerminalBase.BlockEntityBase data))
            return ActionResult.FAIL;
        if (!(blockEntity.data instanceof SchindlerZLine3Keypad1.BlockEntity data1)) return ActionResult.FAIL;

        // 获取按键映射
        SchindlerZLine3Keypad1KeyMapping mapping = new SchindlerZLine3Keypad1KeyMapping();
        String output = mapping.mapping(data.getScreenId(), transformedX, hitY);

        if (player.isHolding(top.xfunny.Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(top.xfunny.Items.YTE_LIFT_BUTTONS_LINK_REMOVER.get()) || player.isHolding(top.xfunny.Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER.get())) {
            Init.LOGGER.info("onUse2");
            return ActionResult.PASS;
        } else {
            // 处理输入逻辑
            processKeyInput(world, pos, data1, data, data.getScreenId(), output);

            player.sendMessage(Text.of("screenId: " + data.getScreenId() + " 您点击了：" + output), true);

        }
        return ActionResult.SUCCESS;
    }

    private void processKeyInput(World world, BlockPos pos, SchindlerZLine3Keypad1.BlockEntity data1, LiftDestinationDispatchTerminalBase.BlockEntityBase data, String screenId, String output) {
        Map<String, Integer> numberKeys = Map.of(
                "number1", 1, "number2", 2, "number3", 3,
                "number4", 4, "number5", 5, "number6", 6,
                "number7", 7, "number8", 8, "number9", 9,
                "number0", 0
        );

        BiConsumer<String, String> handleScreenAndInput = (screen, input) -> {
            data1.switchScreen(screen);
            data1.addInputString(world, pos, input, true, data1, data);
        };

        BiConsumer<String, String> handleLiftCall = (screen, callResult) -> {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            data1.switchScreen("schindler_z_line_3_keypad_1_key_mapping_identifier");
            if (!callResult.equals("?")) {
                data1.addInputString(world, pos, ">" + callResult, false, data1, data);
            } else {
                data1.addInputString(world, pos, "??", false, data1, data);
            }
            scheduler.schedule(() -> {
                data1.switchScreen(screen);
                data1.clearInputString();
            }, 3, TimeUnit.SECONDS);
            scheduler.shutdown();
        };

        switch (screenId) {
            case "schindler_z_line_3_keypad_1_key_mapping_home",
                 "schindler_z_line_3_keypad_1_key_mapping_accessibility" -> {
                if (numberKeys.containsKey(output)) {
                    handleScreenAndInput.accept("schindler_z_line_3_keypad_1_key_mapping_input", String.valueOf(numberKeys.get(output)));
                } else if ("basement".equals(output)) {
                    handleScreenAndInput.accept("schindler_z_line_3_keypad_1_key_mapping_input", "-");
                } else if ("accessibility".equals(output)) {
                    data1.switchScreen("schindler_z_line_3_keypad_1_key_mapping_accessibility");
                } else if ("lobby".equals(output)) {
                    String callResult = data.callLift(world, pos, "1");
                    callResult = callResult.equals("?") ? data.callLift(world, pos, "G") : callResult;
                    handleLiftCall.accept("schindler_z_line_3_keypad_1_key_mapping_home", callResult);
                }
            }
            case "schindler_z_line_3_keypad_1_key_mapping_input" -> {
                if (numberKeys.containsKey(output)) {
                    data1.addInputString(world, pos, numberKeys.get(output), true, data1, data);
                } else if ("basement".equals(output)) {
                    data1.addInputString(world, pos, "-", true, data1, data);
                } else if ("accessibility".equals(output)) {
                    data1.addInputString(world, pos, "1", true, data1, data);
                } else if ("lobby".equals(output)) {
                    String callResult = data.callLift(world, pos, "1");
                    callResult = callResult.equals("?") ? data.callLift(world, pos, "G") : callResult;
                    handleLiftCall.accept("schindler_z_line_3_keypad_1_key_mapping_home", callResult);
                }
            }
            case "schindler_z_line_3_keypad_1_key_mapping_identifier" -> {
                if (numberKeys.containsKey(output)) {
                    data1.clearInputString();
                    handleScreenAndInput.accept("schindler_z_line_3_keypad_1_key_mapping_input", String.valueOf(numberKeys.get(output)));
                } else if ("basement".equals(output)) {
                    data1.clearInputString();
                    handleScreenAndInput.accept("schindler_z_line_3_keypad_1_key_mapping_input", "-");
                } else if ("accessibility".equals(output)) {
                    data1.clearInputString();
                    data1.switchScreen("schindler_z_line_3_keypad_1_key_mapping_accessibility");
                } else if ("lobby".equals(output)) {
                    String callResult = data.callLift(world, pos, "1");
                    callResult = callResult.equals("?") ? data.callLift(world, pos, "G") : callResult;
                    handleLiftCall.accept("schindler_z_line_3_keypad_1_key_mapping_home", callResult);
                }
            }
        }
    }

    public static class BlockEntity extends LiftDestinationDispatchTerminalBase.BlockEntityBase {
        public ArrayList<Object> inputString = new ArrayList<>();

        private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        private ScheduledFuture<?> scheduledTask;

        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.SCHINDLER_Z_LINE_3_KEYPAD_1.get(), pos, state);
            super.registerScreenId("schindler_z_line_3_keypad_1_key_mapping_home");//初始化screen
        }

        public void addInputString(World world, BlockPos pos, Object number, boolean callLift, SchindlerZLine3Keypad1.BlockEntity data1, LiftDestinationDispatchTerminalBase.BlockEntityBase data) {
            if (callLift) {
                inputString.add(number);
                if (scheduledTask != null && !scheduledTask.isDone()) {
                    scheduledTask.cancel(false);
                }

                Runnable task = () -> {
                    switchScreen("schindler_z_line_3_keypad_1_key_mapping_identifier");
                    callLift(world, pos, ArrayListToString.arrayListToString(data1.getInputString()));
                    String liftIdentifier = data.getLiftIdentifier();
                    data1.addInputString(world, pos, liftIdentifier.equals("?") ? "??" : "<" + liftIdentifier, false, data1, data);

                    ScheduledExecutorService scheduler1 = Executors.newSingleThreadScheduledExecutor();
                    scheduler1.schedule(() -> {
                        data1.clearInputString();
                        data1.switchScreen("schindler_z_line_3_keypad_1_key_mapping_home");
                    }, 3, TimeUnit.SECONDS);
                    scheduler1.shutdown();
                };

                scheduledTask = scheduler.schedule(task, 3, TimeUnit.SECONDS);
            } else {
                inputString.clear();
                inputString.add(number);
            }
        }

        public void clearInputString() {
            inputString.clear();
        }

        public ArrayList<Object> getInputString() {
            if (inputString.isEmpty()) {
                inputString.add("");
            }
            return inputString;
        }

        public void switchScreen(Object screenId) {
            super.registerScreenId((String) screenId);
        }
    }
}