package top.xfunny;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.registry.ItemRegistryObject;
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

    public static final ItemRegistryObject MITSUBISHI_NEXWAY_DOOR_1;
    public static final ItemRegistryObject SCHINDLER_QKS9_DOOR_1;


    static {

        YTE_LIFT_BUTTONS_LINK_CONNECTOR = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "yte_lift_buttons_link_connector"), itemSettings -> new Item(new YteLiftButtonsLinker(true, itemSettings)), CreativeModeTabs.YTE_TOOLS);
        YTE_LIFT_BUTTONS_LINK_REMOVER = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "yte_lift_buttons_link_remover"), itemSettings -> new Item(new YteLiftButtonsLinker(false, itemSettings)), CreativeModeTabs.YTE_TOOLS);
        YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "yte_group_lift_buttons_link_connector"), itemSettings -> new Item(new YteGroupLiftButtonsLinker(true, itemSettings)), CreativeModeTabs.YTE_TOOLS);
        YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "yte_group_lift_buttons_link_remover"), itemSettings -> new Item(new YteGroupLiftButtonsLinker(false, itemSettings)), CreativeModeTabs.YTE_TOOLS);
        FLOOR_AUTO_SETTER = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "floor_auto_setter"), itemSettings -> new Item(new FloorAutoSetter(itemSettings)), CreativeModeTabs.YTE_TOOLS);

        MITSUBISHI_NEXWAY_DOOR_1 = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "mitsubishi_nexway_door_1"), itemSettings -> new Item(new ItemPSDAPGBase(ItemPSDAPGBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDAPGBase.EnumPSDAPGType.MITSUBISHI_NEXWAY_DOOR_1, itemSettings)), CreativeModeTabs.YTE_LIFT_DOORS);
        SCHINDLER_QKS9_DOOR_1 = Init.REGISTRY.registerItem(new Identifier(Init.MOD_ID, "schindler_qks9_door_1"), itemSettings -> new Item(new ItemPSDAPGBase(ItemPSDAPGBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDAPGBase.EnumPSDAPGType.SCHINDLER_QKS9_DOOR_1, itemSettings)), CreativeModeTabs.YTE_LIFT_DOORS);
    }

    public static void init() {
        Init.LOGGER.info("正在注册物品");
    }

}
