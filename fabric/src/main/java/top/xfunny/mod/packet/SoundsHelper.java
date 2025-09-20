package top.xfunny.mod.packet;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.SoundCategory;
import top.xfunny.mod.SoundEvents;

public final class SoundsHelper {
    public static void playSound(BlockPos blockPos, ServerPlayerEntity serverPlayerEntity, String soundInstruction){
        switch (soundInstruction){// 播放声音
                case "otis_series_1_lantern_up":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.OTIS_SERIES_1_LANTERN_1_UP.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;

                case "otis_series_1_lantern_down":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.OTIS_SERIES_1_LANTERN_1_DOWN.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
                case "schindler_m_series_lantern_1":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.SCHINDLER_M_SERIES_LANTERN_1.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;
            case "mitsubishi_nexway_lantern_1_up":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.MITSUBISHI_NEXWAY_LANTERN_1_UP.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
                break;

            case "mitsubishi_nexway_lantern_1_down":
                serverPlayerEntity.getEntityWorld().playSound(
                        null,
                        blockPos,
                        SoundEvents.MITSUBISHI_NEXWAY_LANTERN_1_DOWN.get(),
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );
        }
    }
}
