package top.xfunny.block.base;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.*;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.MinecraftClientData;
import top.xfunny.Init;
import top.xfunny.LiftFloorRegistry;
import top.xfunny.ButtonRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mtr.core.data.LiftDirection.NONE;

public abstract class LiftDestinationDispatchTerminalBase extends BlockExtension implements DirectionHelper, BlockWithEntity, IBlock {
    public static final BooleanProperty UNLOCKED = BooleanProperty.of("unlocked");
    private final boolean isOdd;

    public LiftDestinationDispatchTerminalBase(boolean isOdd) {
        super(BlockHelper.createBlockSettings(true));
        this.isOdd = isOdd;
    }

    @FunctionalInterface
    public interface FloorLiftCallback {
        void accept(int floor, Lift lift);
    }

    public static void hasButtonsClient(BlockPos trackPosition, FloorLiftCallback callback){
        MinecraftClientData.getInstance().lifts.forEach(lift -> {
            // 获取电梯轨道位置对应的楼层索引
            final int floorIndex = lift.getFloorIndex(Init.blockPosToPosition(trackPosition));

            // 如果楼层索引非负，表示电梯中存在该楼层，执行回调函数
            if (floorIndex >= 0) {
                callback.accept(floorIndex, lift);
            }
        });
    }

    @Nonnull
    @Override
    public abstract ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit);


    @Override
    public BlockState getPlacementState2(ItemPlacementContext ctx) {
        // 获取玩家面对的方向
        final Direction facing = ctx.getPlayerFacing();
        // 根据默认状态和玩家面对的方向来设置方块状态，并返回
        if (!isOdd) {
            return IBlock.isReplaceable(ctx, facing.rotateYClockwise(), 2) ? getDefaultState2().with(new Property<>(FACING.data), facing.data).with(new Property<>(SIDE.data), EnumSide.LEFT) : null;
        } else {
            return getDefaultState2().with(new Property<>(FACING.data), facing.data);
        }
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            final Direction facing = IBlock.getStatePropertySafe(state, FACING);
            if (!isOdd) {
                world.setBlockState(pos.offset(facing.rotateYClockwise()), getDefaultState2().with(new Property<>(FACING.data), facing.data).with(new Property<>(SIDE.data), EnumSide.RIGHT), 3);
            }
            world.updateNeighbors(pos, Blocks.getAirMapped());
            state.updateNeighbors(new WorldAccess(world.data), pos, 3);
        }
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!isOdd) {
            if (IBlock.getStatePropertySafe(state, SIDE) == EnumSide.RIGHT) {
                IBlock.onBreakCreative(world, player, pos.offset(IBlock.getSideDirection(state)));
            }else if(IBlock.getStatePropertySafe(state, SIDE) == EnumSide.LEFT){
                IBlock.onBreakCreative(world, player, pos.offset(IBlock.getSideDirection(state)));
            }
        }
        super.onBreak2(world, pos, state, player);
    }


    public static class BlockEntityBase extends BlockEntityExtension implements LiftFloorRegistry, ButtonRegistry {
        // 用于在CompoundTag中标识地板位置数组的键
        private static final String KEY_TRACK_FLOOR_POS = "track_floor_pos";
        private static final String KEY_LIFT_BUTTON_POSITIONS = "lift_button_position";
        private static final String KEY_SCREEN_ID = "screen_id";


        public final ObjectOpenHashSet<BlockPos> liftButtonPositions = new ObjectOpenHashSet<>();
        private final ObjectOpenHashSet<BlockPos> trackPositions = new ObjectOpenHashSet<>();
        private String screenId;

        public LiftDirection liftDirection = NONE;

        public BlockPos selfPos;

        private LiftDirection pressedButtonDirection;

        public BlockEntityBase(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
            super(type, blockPos, blockState);
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {

            // 清空当前位置集合，准备加载新的数据
            trackPositions.clear();
            liftButtonPositions.clear();

            // 从CompoundTag中读取名为KEY_TRACK_FLOOR_POS的长整型数组
            // 每个长整型代表一个BlockPos位置，将其转换并添加到trackPositions集合中
            for (final long position : compoundTag.getLongArray(KEY_TRACK_FLOOR_POS)) {
                trackPositions.add(BlockPos.fromLong(position));
            }

            for (final long position : compoundTag.getLongArray(KEY_LIFT_BUTTON_POSITIONS)) {
                liftButtonPositions.add(BlockPos.fromLong(position));
            }
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            // 创建一个临时的List，用于存储trackPositions的长整型表示
            final List<Long> trackPositionsList = new ArrayList<>();

            // 遍历trackPositions集合，将每个位置转换为长整型并添加到trackPositionsList中
            // 这里的转换是为了以长整型数组的形式存储这些位置信息
            trackPositions.forEach(position -> {
                trackPositionsList.add(position.asLong());
            });
            // 将收集到的trackPositions长整型列表以数组的形式存储到compoundTag中
            // 使用的键是KEY_TRACK_FLOOR_POS，值是trackPositionsList数组
            compoundTag.putLongArray(KEY_TRACK_FLOOR_POS, trackPositionsList);


            final List<Long> liftButtonPositionsList = new ArrayList<>();
            liftButtonPositions.forEach(position -> {
                liftButtonPositionsList.add(position.asLong());
            });
            compoundTag.putLongArray(KEY_LIFT_BUTTON_POSITIONS, liftButtonPositionsList);

            compoundTag.putString(KEY_SCREEN_ID,screenId);
        }

        public void registerFloor(BlockPos selfPos, World world, BlockPos pos, boolean isAdd) {
            Init.LOGGER.info("正在操作");
            this.selfPos = selfPos;
            if (IBlock.getStatePropertySafe(world, getPos2(), SIDE) == EnumSide.RIGHT) {
                final BlockEntity blockEntity = world.getBlockEntity(getPos2().offset(IBlock.getStatePropertySafe(world, getPos2(), FACING).rotateYCounterclockwise()));
                if (blockEntity != null && blockEntity.data instanceof LiftButtonsBase.BlockEntityBase) {
                    ((LiftButtonsBase.BlockEntityBase) blockEntity.data).registerFloor(selfPos, world, pos, isAdd);
                }
            } else {
                if (isAdd) {
                    // 如果是添加操作，则将位置添加到跟踪列表中
                    trackPositions.add(pos);
                    Init.LOGGER.info("已添加");
                } else {
                    // 如果是非添加操作，则从跟踪列表中移除该位置
                    trackPositions.remove(pos);
                    Init.LOGGER.info("已移除");
                }
            }
            // 更新数据状态，标记数据为“脏”，表示需要保存或同步
            markDirty2();
        }

        @Override
        public void registerButton(World world, BlockPos blockPos, boolean isAdd) {
            Init.LOGGER.info("正在进行外呼关联");

            if (IBlock.getStatePropertySafe(world, getPos2(), SIDE) == EnumSide.RIGHT) {
                final BlockEntity blockEntity = world.getBlockEntity(getPos2().offset(IBlock.getStatePropertySafe(world, getPos2(), FACING).rotateYCounterclockwise()));
                if (blockEntity != null && blockEntity.data instanceof LiftButtonsBase.BlockEntityBase) {
                    ((LiftButtonsBase.BlockEntityBase) blockEntity.data).registerButton(world, blockPos, isAdd);
                }
            }else{
                if (isAdd) {
                    // 如果是添加操作，则将位置添加到跟踪列表中
                    liftButtonPositions.add(blockPos);
                    Init.LOGGER.info("已添加到站灯" + blockPos.toShortString());
                    Init.LOGGER.info("lanternPositions" + liftButtonPositions);
                } else {
                    // 如果是非添加操作，则从跟踪列表中移除该位置
                    liftButtonPositions.remove(blockPos);
                    Init.LOGGER.info("已移除到站灯");
                }
            }


            markDirty2();
        }

        public void registerScreenId(String screenId){
            this.screenId = screenId;
        }

        public String getScreenId() {
            return screenId;
        }

        public void forEachTrackPosition(Consumer<BlockPos> consumer) {
            trackPositions.forEach(consumer);
        }

        public void forEachLiftButtonPosition(Consumer<BlockPos> consumer) {
            liftButtonPositions.forEach(consumer);
        }

        public ObjectOpenHashSet<BlockPos> getLiftButtonPositions() {
            return liftButtonPositions;
        }

        public LiftDirection getPressedButtonDirection() {
            return pressedButtonDirection;
        }

        public void setPressedButtonDirection(LiftDirection direction) {
            this.pressedButtonDirection = direction;
        }


    }




}
