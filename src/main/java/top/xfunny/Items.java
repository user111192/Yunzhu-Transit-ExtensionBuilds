package top.xfunny;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.registry.ItemRegistryObject;
import org.mtr.mod.CreativeModeTabs;

import top.xfunny.item.FloorAutoSetter;
import top.xfunny.item.ItemPSDAPGBase;
import top.xfunny.item.YteGroupLiftButtonsLinker;
import top.xfunny.item.YteLiftButtonsLinker;

public class Items {
    public static final ItemRegistryObject YTE_LIFT_BUTTONS_LINK_CONNECTOR;
    public static final ItemRegistryObject YTE_LIFT_BUTTONS_LINK_REMOVER;
    public static final ItemRegistryObject YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR;
    public static final ItemRegistryObject YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER;
    public static final ItemRegistryObject FLOOR_AUTO_SETTER;

    public static final ItemRegistryObject SCHINDLER_QKS9_DOOR_1;



    static {

        YTE_LIFT_BUTTONS_LINK_CONNECTOR = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "yte_lift_buttons_link_connector"), itemSettings -> new Item(new YteLiftButtonsLinker(true, itemSettings)), CreativeModeTabs.ESCALATORS_LIFTS);
        YTE_LIFT_BUTTONS_LINK_REMOVER = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "yte_lift_buttons_link_remover"), itemSettings -> new Item(new YteLiftButtonsLinker(false, itemSettings)), CreativeModeTabs.ESCALATORS_LIFTS);
        YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "yte_group_lift_buttons_link_connector"), itemSettings -> new Item(new YteGroupLiftButtonsLinker(true, itemSettings)), CreativeModeTabs.ESCALATORS_LIFTS);
        YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "yte_group_lift_buttons_link_remover"), itemSettings -> new Item(new YteGroupLiftButtonsLinker(false, itemSettings)), CreativeModeTabs.ESCALATORS_LIFTS);
        FLOOR_AUTO_SETTER = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "floor_auto_setter"), itemSettings -> new Item(new FloorAutoSetter(itemSettings)), CreativeModeTabs.ESCALATORS_LIFTS);

        SCHINDLER_QKS9_DOOR_1 = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "schindler_qks9_door_1"), itemSettings -> new Item(new ItemPSDAPGBase(ItemPSDAPGBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDAPGBase.EnumPSDAPGType.SCHINDLER_QKS9_DOOR_1, itemSettings)), CreativeModeTabs.ESCALATORS_LIFTS);
    }

    public static void init() {
        Init.LOGGER.info("正在注册物品");
    }

}
