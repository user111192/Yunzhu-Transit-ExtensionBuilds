package top.xfunny;

import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.BlockHelper;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mod.CreativeModeTabs;
import top.xfunny.block.*;


public class Blocks {


    public static final BlockRegistryObject TEST_LIFT_BUTTONS;
    public static final BlockRegistryObject TEST_LIFT_BUTTONS_WITHOUT_SCREEN;
    public static final BlockRegistryObject OTIS_SERIES_1_BUTTON_1;
    public static final BlockRegistryObject OTIS_SERIES_1_BUTTON_2;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_1;
    public static final BlockRegistryObject MITSUBISHI_NEXWAY_BUTTON_2;

    public static final BlockRegistryObject SCHINDLER_D_SERIES_D2BUTTON;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_BUTTON;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_TOUCH_BUTTON;
    public static final BlockRegistryObject SCHINDLER_S_SERIES_GREY_BUTTON;

    public static final BlockRegistryObject TEST_LIFT_PANEL;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_SCREEN_1;
    public static final BlockRegistryObject OTIS_SERIES_1_SCREEN_1;

    public static final BlockRegistryObject TEST_LIFT_HALL_LANTERNS;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_SCREEN_2_ODD;
    public static final BlockRegistryObject SCHINDLER_M_SERIES_SCREEN_2_EVEN;

    public static final BlockRegistryObject PAT_P01_TICKET_BARRIER_ENTRANCE;
    public static final BlockRegistryObject PAT_P01_TICKET_BARRIER_EXIT;
    public static final BlockRegistryObject PAT_P01_TICKET_BARRIER_BARE;

    public static final BlockRegistryObject LIFT_TRACK_EMPTY_FLOOR;

    public static final BlockRegistryObject SCHINDLER_QKS9_DOOR_1;


    static {

        TEST_LIFT_BUTTONS = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "test_lift_buttons"), () -> new Block(new TestLiftButtons()), CreativeModeTabs.ESCALATORS_LIFTS);
        MITSUBISHI_NEXWAY_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_1"), () -> new Block(new MitsubishiNexWayButton1()), CreativeModeTabs.ESCALATORS_LIFTS);
        MITSUBISHI_NEXWAY_BUTTON_2 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_button_2"), () -> new Block(new MitsubishiNexWayButton2()), CreativeModeTabs.ESCALATORS_LIFTS);
        TEST_LIFT_HALL_LANTERNS = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "test_lift_hall_lanterns"), () -> new Block(new TestLiftHallLanterns()), CreativeModeTabs.ESCALATORS_LIFTS);
        TEST_LIFT_PANEL = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "test_lift_panel"), () -> new Block(new TestLiftPanel()), CreativeModeTabs.ESCALATORS_LIFTS);
        TEST_LIFT_BUTTONS_WITHOUT_SCREEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "test_lift_buttons_without_screen"), () -> new Block(new TestLiftButtonsWithoutScreen()), CreativeModeTabs.ESCALATORS_LIFTS);
        OTIS_SERIES_1_BUTTON_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_1_button_1"), () -> new Block(new OtisSeries1Button()), CreativeModeTabs.ESCALATORS_LIFTS);
        OTIS_SERIES_1_BUTTON_2 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_1_button_2"), () -> new Block(new OtisSeries1Button()), CreativeModeTabs.ESCALATORS_LIFTS);
        OTIS_SERIES_1_SCREEN_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "otis_series_1_screen_1"), () -> new Block(new OtisSeries1Screen()), CreativeModeTabs.ESCALATORS_LIFTS);
        SCHINDLER_D_SERIES_D2BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_d_series_d2button"), () -> new Block(new SchindlerDSeriesD2Button()), CreativeModeTabs.ESCALATORS_LIFTS);
        SCHINDLER_M_SERIES_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_button"), () -> new Block(new SchindlerMSeriesButton()), CreativeModeTabs.ESCALATORS_LIFTS);
        SCHINDLER_M_SERIES_TOUCH_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_touch_button"), () -> new Block(new SchindlerMSeriesTouchButton()), CreativeModeTabs.ESCALATORS_LIFTS);
        SCHINDLER_M_SERIES_SCREEN_1 = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_screen_1"), () -> new Block(new SchindlerMSeriesScreen1()), CreativeModeTabs.ESCALATORS_LIFTS);
        SCHINDLER_M_SERIES_SCREEN_2_ODD = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_screen_2_odd"), () -> new Block(new SchindlerMSeriesScreen2Odd()), CreativeModeTabs.ESCALATORS_LIFTS);
        SCHINDLER_M_SERIES_SCREEN_2_EVEN = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_m_series_screen_2_even"), () -> new Block(new SchindlerMSeriesScreen2Even()), CreativeModeTabs.ESCALATORS_LIFTS);
        SCHINDLER_S_SERIES_GREY_BUTTON = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "schindler_s_series_grey_button"), () -> new Block(new SchindlerSSeriesGreyButton()), CreativeModeTabs.ESCALATORS_LIFTS);
        PAT_P01_TICKET_BARRIER_ENTRANCE = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_p01_ticket_barrier_entrance"), () -> new Block(new PATTicketBarrier(true)), CreativeModeTabs.RAILWAY_FACILITIES);
        PAT_P01_TICKET_BARRIER_EXIT = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_p01_ticket_barrier_exit"), () -> new Block(new PATTicketBarrier(false)), CreativeModeTabs.RAILWAY_FACILITIES);
        PAT_P01_TICKET_BARRIER_BARE = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "pat_p01_ticket_barrier_bare"), () -> new Block(new PATTicketBarrierBareBlock(BlockHelper.createBlockSettings(false).strength(4.0f).nonOpaque())), CreativeModeTabs.RAILWAY_FACILITIES);
        LIFT_TRACK_EMPTY_FLOOR = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "lift_track_empty_floor"), () -> new Block(new EmptyFloor()), CreativeModeTabs.ESCALATORS_LIFTS);


        SCHINDLER_QKS9_DOOR_1 = Init.REGISTRY.registerBlock(new Identifier(Init.MOD_ID, "schindler_qks9_door_1"), () -> new Block(new SchindlerQKS9Door1()));
    }


    public static void init() {
        Init.LOGGER.info("正在注册方块");
    }
}
