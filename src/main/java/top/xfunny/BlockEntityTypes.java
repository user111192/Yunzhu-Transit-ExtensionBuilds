package top.xfunny;

import org.mtr.mapping.registry.BlockEntityTypeRegistryObject;

import org.mtr.mapping.holder.Identifier;



import top.xfunny.Block.TestLiftButtons;
import top.xfunny.Block.TestLiftButtonsWithoutScreen;

public class BlockEntityTypes {

    public static final BlockEntityTypeRegistryObject<TestLiftButtons.BlockEntity> TEST_LIFT_BUTTONS;
    public static final BlockEntityTypeRegistryObject<TestLiftButtonsWithoutScreen.BlockEntity> TEST_LIFT_BUTTONS_WITHOUT_SCREEN;


    static {

        TEST_LIFT_BUTTONS = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_buttons"), TestLiftButtons.BlockEntity::new, Blocks.TEST_LIFT_BUTTONS::get);
        TEST_LIFT_BUTTONS_WITHOUT_SCREEN = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_buttons_without_screen"), TestLiftButtonsWithoutScreen.BlockEntity::new, Blocks.TEST_LIFT_BUTTONS_WITHOUT_SCREEN::get);
    }

    public static void init() {
        Init.LOGGER.info("正在注册方块实体");
    }
}
