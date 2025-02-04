package top.xfunny;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.BlockEntityTypeRegistryObject;
import top.xfunny.block.*;

public class BlockEntityTypes {

    public static final BlockEntityTypeRegistryObject<TestLiftButtons.BlockEntity> TEST_LIFT_BUTTONS;
    public static final BlockEntityTypeRegistryObject<TestLiftHallLanterns.BlockEntity> TEST_LIFT_HALL_LANTERNS;
    public static final BlockEntityTypeRegistryObject<TestLiftButtonsWithoutScreen.BlockEntity> TEST_LIFT_BUTTONS_WITHOUT_SCREEN;
    public static final BlockEntityTypeRegistryObject<TestLiftPanel.BlockEntity> TEST_LIFT_PANEL;
    public static final BlockEntityTypeRegistryObject<TestLiftDestinationDispatchTerminal.BlockEntity> TEST_LIFT_DESTINATION_DISPATCH_TERMINAL;

    public static final BlockEntityTypeRegistryObject<MitsubishiNexWayButton1.BlockEntity> MITSUBISHI_NEXWAY_BUTTON_1;
    public static final BlockEntityTypeRegistryObject<MitsubishiNexWayButton1WithoutScreen.BlockEntity> MITSUBISHI_NEXWAY_BUTTON_1_WITHOUT_SCREEN;
    public static final BlockEntityTypeRegistryObject<MitsubishiNexWayButton2.BlockEntity> MITSUBISHI_NEXWAY_BUTTON_2;
    public static final BlockEntityTypeRegistryObject<MitsubishiNexWayButton2WithoutScreen.BlockEntity> MITSUBISHI_NEXWAY_BUTTON_2_WITHOUT_SCREEN;
    public static final BlockEntityTypeRegistryObject<MitsubishiNexWayButton3.BlockEntity> MITSUBISHI_NEXWAY_BUTTON_3;
    public static final BlockEntityTypeRegistryObject<MitsubishiNexWayButton3WithoutScreen.BlockEntity> MITSUBISHI_NEXWAY_BUTTON_3_WITHOUT_SCREEN;
    public static final BlockEntityTypeRegistryObject<OtisSeries1Button.BlockEntity> OTIS_SERIES_1_BUTTON_1;
    public static final BlockEntityTypeRegistryObject<OtisSeries1Button.BlockEntity> OTIS_SERIES_1_BUTTON_2;
    public static final BlockEntityTypeRegistryObject<OtisSeries1Screen.BlockEntity> OTIS_SERIES_1_SCREEN_1;
    public static final BlockEntityTypeRegistryObject<SchindlerDSeriesD2Button.BlockEntity> SCHINDLER_D_SERIES_D2BUTTON;
    public static final BlockEntityTypeRegistryObject<SchindlerDSeriesScreen1Odd.BlockEntity> SCHINDLER_D_SERIES_SCREEN_1_ODD;
    public static final BlockEntityTypeRegistryObject<SchindlerDSeriesScreen1Even.BlockEntity> SCHINDLER_D_SERIES_SCREEN_1_EVEN;
    public static final BlockEntityTypeRegistryObject<SchindlerDSeriesScreen2GreenOdd.BlockEntity> SCHINDLER_D_SERIES_SCREEN_2_GREEN_ODD;
    public static final BlockEntityTypeRegistryObject<SchindlerDSeriesScreen2GreenEven.BlockEntity> SCHINDLER_D_SERIES_SCREEN_2_GREEN_EVEN;
    public static final BlockEntityTypeRegistryObject<SchindlerDSeriesScreen2BlueOdd.BlockEntity> SCHINDLER_D_SERIES_SCREEN_2_BLUE_ODD;
    public static final BlockEntityTypeRegistryObject<SchindlerDSeriesScreen2BlueEven.BlockEntity> SCHINDLER_D_SERIES_SCREEN_2_BLUE_EVEN;
    public static final BlockEntityTypeRegistryObject<SchindlerDSeriesScreen2RedOdd.BlockEntity> SCHINDLER_D_SERIES_SCREEN_2_RED_ODD;
    public static final BlockEntityTypeRegistryObject<SchindlerDSeriesScreen2RedEven.BlockEntity> SCHINDLER_D_SERIES_SCREEN_2_RED_EVEN;


     public static final BlockEntityTypeRegistryObject<SchindlerMSeriesButton.BlockEntity> SCHINDLER_M_SERIES_BUTTON;
    public static final BlockEntityTypeRegistryObject<SchindlerMSeriesTouchButton.BlockEntity> SCHINDLER_M_SERIES_TOUCH_BUTTON;
    public static final BlockEntityTypeRegistryObject<SchindlerMSeriesScreen1.BlockEntity> SCHINDLER_M_SERIES_SCREEN_1;
    public static final BlockEntityTypeRegistryObject<SchindlerMSeriesScreen2Odd.BlockEntity> SCHINDLER_M_SERIES_SCREEN_2_ODD;
    public static final BlockEntityTypeRegistryObject<SchindlerMSeriesScreen2Even.BlockEntity> SCHINDLER_M_SERIES_SCREEN_2_EVEN;
    public static final BlockEntityTypeRegistryObject<SchindlerSSeriesGreyButton.BlockEntity> SCHINDLER_S_SERIES_GREY_BUTTON;
    public static final BlockEntityTypeRegistryObject<HitachiB85Button1.BlockEntity> HITACHI_B85_BUTTON_1;

    public static final BlockEntityTypeRegistryObject<MitsubishiNexWayDoor1.BlockEntity> MITSUBISHI_NEXWAY_DOOR_1;
    public static final BlockEntityTypeRegistryObject<SchindlerQKS9Door1.BlockEntity> SCHINDLER_QKS9_DOOR_1;

    public static final BlockEntityTypeRegistryObject<EmptyFloor.BlockEntity> LIFT_TRACK_EMPTY_FLOOR;


    static {
        TEST_LIFT_BUTTONS = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_buttons"), TestLiftButtons.BlockEntity::new, Blocks.TEST_LIFT_BUTTONS::get);
        TEST_LIFT_HALL_LANTERNS = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_hall_lanterns"), TestLiftHallLanterns.BlockEntity::new, Blocks.TEST_LIFT_HALL_LANTERNS::get);
        TEST_LIFT_BUTTONS_WITHOUT_SCREEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_buttons_without_screen"), TestLiftButtonsWithoutScreen.BlockEntity::new, Blocks.TEST_LIFT_BUTTONS_WITHOUT_SCREEN::get);
        TEST_LIFT_PANEL = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_panel"), TestLiftPanel.BlockEntity::new, Blocks.TEST_LIFT_PANEL::get);
        TEST_LIFT_DESTINATION_DISPATCH_TERMINAL = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_destination_dispatch_terminal"), TestLiftDestinationDispatchTerminal.BlockEntity::new, Blocks.TEST_LIFT_DESTINATION_DISPATCH_TERMINAL::get);


        MITSUBISHI_NEXWAY_BUTTON_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_1"), MitsubishiNexWayButton1.BlockEntity::new, Blocks.MITSUBISHI_NEXWAY_BUTTON_1::get);
        MITSUBISHI_NEXWAY_BUTTON_1_WITHOUT_SCREEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_1_without_screen"), MitsubishiNexWayButton1WithoutScreen.BlockEntity::new, Blocks.MITSUBISHI_NEXWAY_BUTTON_1_WITHOUT_SCREEN::get);
        MITSUBISHI_NEXWAY_BUTTON_2 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_2"), MitsubishiNexWayButton2.BlockEntity::new, Blocks.MITSUBISHI_NEXWAY_BUTTON_2::get);
        MITSUBISHI_NEXWAY_BUTTON_2_WITHOUT_SCREEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_2_without_screen"), MitsubishiNexWayButton2WithoutScreen.BlockEntity::new, Blocks.MITSUBISHI_NEXWAY_BUTTON_2_WITHOUT_SCREEN::get);
        MITSUBISHI_NEXWAY_BUTTON_3 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_3"), MitsubishiNexWayButton3.BlockEntity::new, Blocks.MITSUBISHI_NEXWAY_BUTTON_3::get);
        MITSUBISHI_NEXWAY_BUTTON_3_WITHOUT_SCREEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_3_without_screen"), MitsubishiNexWayButton3WithoutScreen.BlockEntity::new, Blocks.MITSUBISHI_NEXWAY_BUTTON_3_WITHOUT_SCREEN::get);
        OTIS_SERIES_1_BUTTON_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "otis_series_1_button_1"), OtisSeries1Button.BlockEntity::new, Blocks.OTIS_SERIES_1_BUTTON_1::get);
        OTIS_SERIES_1_BUTTON_2 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "otis_series_1_button_2"), OtisSeries1Button.BlockEntity::new, Blocks.OTIS_SERIES_1_BUTTON_2::get);
        OTIS_SERIES_1_SCREEN_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "otis_series_1_screen_1"), OtisSeries1Screen.BlockEntity::new, Blocks.OTIS_SERIES_1_SCREEN_1::get);
        SCHINDLER_D_SERIES_D2BUTTON = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_d_series_d2button.json"), SchindlerDSeriesD2Button.BlockEntity::new, Blocks.SCHINDLER_D_SERIES_D2BUTTON::get);
        SCHINDLER_D_SERIES_SCREEN_1_ODD = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_d_series_screen_1_odd"), SchindlerDSeriesScreen1Odd.BlockEntity::new, Blocks.SCHINDLER_D_SERIES_SCREEN_1_ODD::get);
        SCHINDLER_D_SERIES_SCREEN_1_EVEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_d_series_screen_1_even"), SchindlerDSeriesScreen1Even.BlockEntity::new, Blocks.SCHINDLER_D_SERIES_SCREEN_1_EVEN::get);
        SCHINDLER_D_SERIES_SCREEN_2_GREEN_ODD = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_green_odd"), SchindlerDSeriesScreen2GreenOdd.BlockEntity::new, Blocks.SCHINDLER_D_SERIES_SCREEN_2_GREEN_ODD::get);
        SCHINDLER_D_SERIES_SCREEN_2_GREEN_EVEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_green_even"), SchindlerDSeriesScreen2GreenEven.BlockEntity::new, Blocks.SCHINDLER_D_SERIES_SCREEN_2_GREEN_EVEN::get);
        SCHINDLER_D_SERIES_SCREEN_2_BLUE_ODD = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_blue_odd"), SchindlerDSeriesScreen2BlueOdd.BlockEntity::new, Blocks.SCHINDLER_D_SERIES_SCREEN_2_BLUE_ODD::get);
        SCHINDLER_D_SERIES_SCREEN_2_BLUE_EVEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_blue_even"), SchindlerDSeriesScreen2BlueEven.BlockEntity::new, Blocks.SCHINDLER_D_SERIES_SCREEN_2_BLUE_EVEN::get);
        SCHINDLER_D_SERIES_SCREEN_2_RED_ODD = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_red_odd"), SchindlerDSeriesScreen2RedOdd.BlockEntity::new, Blocks.SCHINDLER_D_SERIES_SCREEN_2_RED_ODD::get);
        SCHINDLER_D_SERIES_SCREEN_2_RED_EVEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_d_series_screen_2_red_even"), SchindlerDSeriesScreen2RedEven.BlockEntity::new, Blocks.SCHINDLER_D_SERIES_SCREEN_2_RED_EVEN::get);

        SCHINDLER_M_SERIES_BUTTON = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_m_series_button.json"), SchindlerMSeriesButton.BlockEntity::new, Blocks.SCHINDLER_M_SERIES_BUTTON::get);
        SCHINDLER_M_SERIES_TOUCH_BUTTON = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_m_series_touch_button.json"), SchindlerMSeriesTouchButton.BlockEntity::new, Blocks.SCHINDLER_M_SERIES_TOUCH_BUTTON::get);
        SCHINDLER_M_SERIES_SCREEN_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_m_series_screen_1"), SchindlerMSeriesScreen1.BlockEntity::new, Blocks.SCHINDLER_M_SERIES_SCREEN_1::get);
        SCHINDLER_M_SERIES_SCREEN_2_ODD = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_m_series_screen_2_odd"), SchindlerMSeriesScreen2Odd.BlockEntity::new, Blocks.SCHINDLER_M_SERIES_SCREEN_2_ODD::get);
        SCHINDLER_M_SERIES_SCREEN_2_EVEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_m_series_screen_2_even"), SchindlerMSeriesScreen2Even.BlockEntity::new, Blocks.SCHINDLER_M_SERIES_SCREEN_2_EVEN::get);
        SCHINDLER_S_SERIES_GREY_BUTTON = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_s_series_grey_button"), SchindlerSSeriesGreyButton.BlockEntity::new, Blocks.SCHINDLER_S_SERIES_GREY_BUTTON::get);
        HITACHI_B85_BUTTON_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "hitachi_b85_button_1"), HitachiB85Button1.BlockEntity::new, Blocks.HITACHI_B85_BUTTON_1::get);

        MITSUBISHI_NEXWAY_DOOR_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "mitsubishi_nexway_door_1"), MitsubishiNexWayDoor1.BlockEntity::new, Blocks.MITSUBISHI_NEXWAY_DOOR_1::get);
        SCHINDLER_QKS9_DOOR_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "schindler_qks9_door_1"), SchindlerQKS9Door1.BlockEntity::new, Blocks.SCHINDLER_QKS9_DOOR_1::get);

        LIFT_TRACK_EMPTY_FLOOR = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_track_empty_floor"), EmptyFloor.BlockEntity::new, Blocks.LIFT_TRACK_EMPTY_FLOOR::get);

    }

    public static void init() {
        Init.LOGGER.info("正在注册方块实体");
    }
}