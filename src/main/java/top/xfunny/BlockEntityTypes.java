package top.xfunny;

import org.mtr.mapping.registry.BlockEntityTypeRegistryObject;

import org.mtr.mapping.holder.Identifier;



import top.xfunny.Block.TestLiftButtons;

public class BlockEntityTypes {

    public static final BlockEntityTypeRegistryObject<TestLiftButtons.BlockEntity> TEST_LIFT_BUTTONS;


    static {

        TEST_LIFT_BUTTONS = Init.REGISTRY.registerBlockEntityType(new Identifier(Init.MOD_ID, "lift_buttons"), TestLiftButtons.BlockEntity::new, Blocks.TEST_LIFT_BUTTONS::get);
    }

    public static void init() {
        Init.LOGGER.info("正在注册方块实体");
    }
}
