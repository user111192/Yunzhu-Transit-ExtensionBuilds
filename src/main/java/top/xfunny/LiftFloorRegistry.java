package top.xfunny;

import org.mtr.mapping.holder.BlockPos;

public interface LiftFloorRegistry {
    void registerFloor(BlockPos blockPos, boolean isAdd);

}
