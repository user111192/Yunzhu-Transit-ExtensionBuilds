package top.xfunny;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.registry.ItemRegistryObject;
import org.mtr.mod.CreativeModeTabs;


import top.xfunny.Item.YteLiftButtonsLink;

public class Items {
    public static final ItemRegistryObject YTE_LIFT_BUTTONS_LINK_CONNECTOR;
	public static final ItemRegistryObject YTE_LIFT_BUTTONS_LINK_REMOVER;

    static {

        YTE_LIFT_BUTTONS_LINK_CONNECTOR = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "yte_lift_buttons_link_connector"), itemSettings -> new Item(new YteLiftButtonsLink(true, itemSettings)), CreativeModeTabs.ESCALATORS_LIFTS);
		YTE_LIFT_BUTTONS_LINK_REMOVER = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "yte_lift_buttons_link_remover"), itemSettings -> new Item(new YteLiftButtonsLink(false, itemSettings)), CreativeModeTabs.ESCALATORS_LIFTS);

    }

    public static void init() {
        Init.LOGGER.info("正在注册物品");
    }

}
