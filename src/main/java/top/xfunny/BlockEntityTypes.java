package top.xfunny;

import org.mtr.mapping.registry.BlockEntityTypeRegistryObject;

import org.mtr.mapping.holder.Identifier;


import top.xfunny.block.*;

public class BlockEntityTypes {

    public static final BlockEntityTypeRegistryObject<TestLiftButtons.BlockEntity> TEST_LIFT_BUTTONS;
    public static final BlockEntityTypeRegistryObject<TestLiftHallLanterns.BlockEntity> TEST_LIFT_HALL_LANTERNS;
    public static final BlockEntityTypeRegistryObject<TestLiftButtonsWithoutScreen.BlockEntity> TEST_LIFT_BUTTONS_WITHOUT_SCREEN;
    public static final BlockEntityTypeRegistryObject<TestLiftPanel.BlockEntity> TEST_LIFT_PANEL;
    public static final BlockEntityTypeRegistryObject<OtisSeries1Button.BlockEntity> OTIS_SERIES_1_BUTTON_1;
    public static final BlockEntityTypeRegistryObject<OtisSeries1Button.BlockEntity> OTIS_SERIES_1_BUTTON_2;
    public static final BlockEntityTypeRegistryObject<OtisSeries1Screen.BlockEntity> OTIS_SERIES_1_SCREEN_1;
    public static final BlockEntityTypeRegistryObject<SchindlerDSeriesD2Button.BlockEntity> SCHINDLER_D_SERIES_D2BUTTON;
    public static final BlockEntityTypeRegistryObject<SchindlerMSeriesButton.BlockEntity> SCHINDLER_M_SERIES_BUTTON;
    public static final BlockEntityTypeRegistryObject<SchindlerMSeriesTouchButton.BlockEntity> SCHINDLER_M_SERIES_TOUCH_BUTTON;
    public static final BlockEntityTypeRegistryObject<SchindlerMSeriesScreen1.BlockEntity> SCHINDLER_M_SERIES_SCREEN_1;
    public static final BlockEntityTypeRegistryObject<SchindlerMSeriesScreen2Odd.BlockEntity> SCHINDLER_M_SERIES_SCREEN_2_ODD;
    public static final BlockEntityTypeRegistryObject<SchindlerMSeriesScreen2Odd.BlockEntity> SCHINDLER_M_SERIES_SCREEN_2_EVEN;

    public static final BlockEntityTypeRegistryObject<EmptyFloor.BlockEntity> LIFT_TRACK_EMPTY_FLOOR;


    static {
        TEST_LIFT_BUTTONS = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_buttons"), TestLiftButtons.BlockEntity::new, Blocks.TEST_LIFT_BUTTONS::get);
        TEST_LIFT_HALL_LANTERNS = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_hall_lanterns"), TestLiftHallLanterns.BlockEntity::new, Blocks.TEST_LIFT_HALL_LANTERNS::get);
        TEST_LIFT_BUTTONS_WITHOUT_SCREEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_buttons_without_screen"), TestLiftButtonsWithoutScreen.BlockEntity::new, Blocks.TEST_LIFT_BUTTONS_WITHOUT_SCREEN::get);
        TEST_LIFT_PANEL = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_panel"), TestLiftPanel.BlockEntity::new, Blocks.TEST_LIFT_PANEL::get);
        OTIS_SERIES_1_BUTTON_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "otis_series_1_button_1"), OtisSeries1Button.BlockEntity::new, Blocks.OTIS_SERIES_1_BUTTON_1::get);
        OTIS_SERIES_1_BUTTON_2 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "otis_series_1_button_2"), OtisSeries1Button.BlockEntity::new, Blocks.OTIS_SERIES_1_BUTTON_2::get);
        OTIS_SERIES_1_SCREEN_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "otis_series_1_screen_1"), OtisSeries1Screen.BlockEntity::new, Blocks.OTIS_SERIES_1_SCREEN_1::get);
        SCHINDLER_D_SERIES_D2BUTTON = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_d_series_d2button.json"), SchindlerDSeriesD2Button.BlockEntity::new, Blocks.SCHINDLER_D_SERIES_D2BUTTON::get);
        SCHINDLER_M_SERIES_BUTTON = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_m_series_button.json"), SchindlerMSeriesButton.BlockEntity::new, Blocks.SCHINDLER_M_SERIES_BUTTON::get);
        SCHINDLER_M_SERIES_TOUCH_BUTTON = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_m_series_touch_button.json"), SchindlerMSeriesTouchButton.BlockEntity::new, Blocks.SCHINDLER_M_SERIES_TOUCH_BUTTON::get);
        SCHINDLER_M_SERIES_SCREEN_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_m_series_screen_1"), SchindlerMSeriesScreen1.BlockEntity::new, Blocks.SCHINDLER_M_SERIES_SCREEN_1::get);
        SCHINDLER_M_SERIES_SCREEN_2_ODD = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_m_series_screen_2_odd"), SchindlerMSeriesScreen2Odd.BlockEntity::new, Blocks.SCHINDLER_M_SERIES_SCREEN_2_ODD::get);
        SCHINDLER_M_SERIES_SCREEN_2_EVEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_m_series_screen_2_even"), SchindlerMSeriesScreen2Even.BlockEntity::new, Blocks.SCHINDLER_M_SERIES_SCREEN_2_EVEN::get);
        LIFT_TRACK_EMPTY_FLOOR = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_track_empty_floor"), EmptyFloor.BlockEntity::new, Blocks.LIFT_TRACK_EMPTY_FLOOR::get);
    }

    public static void init() {
        Init.LOGGER.info("正在注册方块实体");
    }
}