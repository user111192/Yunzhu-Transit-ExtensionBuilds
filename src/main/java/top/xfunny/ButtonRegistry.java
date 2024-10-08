package top.xfunny;

import org.mtr.mapping.holder.BlockPos;

public interface ButtonRegistry {
    void registerButton(BlockPos blockPos, boolean isAdd);
}
