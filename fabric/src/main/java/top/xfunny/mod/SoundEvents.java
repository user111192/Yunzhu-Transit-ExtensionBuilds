package top.xfunny.mod;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.SoundEventRegistryObject;


public class SoundEvents {
    public static final SoundEventRegistryObject OTIS_SERIES_1_LANTERN_1_UP;
    public static final SoundEventRegistryObject OTIS_SERIES_1_LANTERN_1_DOWN;
    public static final SoundEventRegistryObject SCHINDLER_M_SERIES_LANTERN_1;
    public static final SoundEventRegistryObject MITSUBISHI_NEXWAY_LANTERN_1_UP;
    public static final SoundEventRegistryObject MITSUBISHI_NEXWAY_LANTERN_1_DOWN;

    static {
        OTIS_SERIES_1_LANTERN_1_UP = Init.REGISTRY.registerSoundEvent(new Identifier(Init.MOD_ID, "otis_series_1_lantern_1_up"));
        OTIS_SERIES_1_LANTERN_1_DOWN = Init.REGISTRY.registerSoundEvent(new Identifier(Init.MOD_ID, "otis_series_1_lantern_1_down"));
        SCHINDLER_M_SERIES_LANTERN_1 = Init.REGISTRY.registerSoundEvent(new Identifier(Init.MOD_ID, "schindler_m_series_lantern_1"));
        MITSUBISHI_NEXWAY_LANTERN_1_UP = Init.REGISTRY.registerSoundEvent(new Identifier(Init.MOD_ID, "mitsubishi_nexway_lantern_1_up"));
        MITSUBISHI_NEXWAY_LANTERN_1_DOWN = Init.REGISTRY.registerSoundEvent(new Identifier(Init.MOD_ID, "mitsubishi_nexway_lantern_1_down"));
    }

    public static void init() {
        Init.LOGGER.info("注册声音事件");
    }
}
