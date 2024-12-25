package top.xfunny.util;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;

public interface ButtonRegistry {
    void registerButton(World world, BlockPos blockPos, boolean isAdd);
}
