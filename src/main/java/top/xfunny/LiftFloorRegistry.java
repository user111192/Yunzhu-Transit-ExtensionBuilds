package top.xfunny;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;

public interface LiftFloorRegistry {
    void registerFloor(World world, BlockPos blockPos, boolean isAdd);
}
