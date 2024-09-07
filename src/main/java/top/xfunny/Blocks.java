package top.xfunny;

import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mod.CreativeModeTabs;

import top.xfunny.Block.TestLiftButtons;


public class Blocks {


    public static final BlockRegistryObject TEST_LIFT_BUTTONS;

    static {

        TEST_LIFT_BUTTONS = Init.REGISTRY.registerBlockWithBlockItem(new Identifier(Init.MOD_ID, "test_lift_buttons"), () -> new Block(new TestLiftButtons()), CreativeModeTabs.ESCALATORS_LIFTS);

    }


    public static void init() {
		Init.LOGGER.info("正在注册方块");
	}
}
