package top.xfunny;

import org.mtr.mapping.registry.BlockEntityTypeRegistryObject;

import org.mtr.mapping.holder.Identifier;


import top.xfunny.Block.OtisSeries1Button;
import top.xfunny.Block.TestLiftButtons;
import top.xfunny.Block.TestLiftButtonsWithoutScreen;
import top.xfunny.Block.TestLiftHallLanterns;

public class BlockEntityTypes {

    public static final BlockEntityTypeRegistryObject<TestLiftButtons.BlockEntity> TEST_LIFT_BUTTONS;
    public static final BlockEntityTypeRegistryObject<TestLiftHallLanterns.BlockEntity> TEST_LIFT_HALL_LANTERNS;
    public static final BlockEntityTypeRegistryObject<TestLiftButtonsWithoutScreen.BlockEntity> TEST_LIFT_BUTTONS_WITHOUT_SCREEN;
    public static final BlockEntityTypeRegistryObject<OtisSeries1Button.BlockEntity> OTIS_SERIES_1_BUTTON_1;
    public static final BlockEntityTypeRegistryObject<OtisSeries1Button.BlockEntity> OTIS_SERIES_1_BUTTON_2;


    static {
        TEST_LIFT_BUTTONS = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_buttons"), TestLiftButtons.BlockEntity::new, Blocks.TEST_LIFT_BUTTONS::get);
        TEST_LIFT_HALL_LANTERNS = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_hall_lanterns"), TestLiftHallLanterns.BlockEntity::new, Blocks.TEST_LIFT_HALL_LANTERNS::get);
        TEST_LIFT_BUTTONS_WITHOUT_SCREEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_buttons_without_screen"), TestLiftButtonsWithoutScreen.BlockEntity::new, Blocks.TEST_LIFT_BUTTONS_WITHOUT_SCREEN::get);
        OTIS_SERIES_1_BUTTON_1 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "otis_series_1_button_1"), OtisSeries1Button.BlockEntity::new, Blocks.OTIS_SERIES_1_BUTTON_1::get);
        OTIS_SERIES_1_BUTTON_2 = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "otis_series_1_button_2"), OtisSeries1Button.BlockEntity::new, Blocks.OTIS_SERIES_1_BUTTON_2::get);
    }

    public static void init() {
        Init.LOGGER.info("正在注册方块实体");
    }
}
