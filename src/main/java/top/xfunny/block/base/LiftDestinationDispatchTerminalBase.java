package top.xfunny.block.base;

import org.mtr.core.data.Lift;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BooleanProperty;
import org.mtr.mapping.mapper.*;
import org.mtr.mod.block.IBlock;

public abstract class LiftDestinationDispatchTerminalBase extends BlockExtension implements DirectionHelper, BlockWithEntity, IBlock {
    public static final BooleanProperty UNLOCKED = BooleanProperty.of("unlocked");

    public LiftDestinationDispatchTerminalBase(BlockSettings settings) {
        super(BlockHelper.createBlockSettings(true));
    }

    @FunctionalInterface
    public interface FloorLiftCallback {
        void accept(int floor, Lift lift);
    }

    public static void hasButtonsClient(BlockPos trackPosition, LiftButtonsBase.FloorLiftCallback callback){

    }


}
