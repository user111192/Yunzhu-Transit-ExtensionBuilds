package top.xfunny;


import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.RegistryClient;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.item.ItemBlockClickingBase;
import top.xfunny.render.*;


public final class InitClient {
    public static final RegistryClient REGISTRY_CLIENT = new RegistryClient(Init.REGISTRY);
    private static long lastMillis = 0;
    private static long gameMillis = 0;


    public static void init() {
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_BUTTONS, RenderTestLiftButtons4::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_HALL_LANTERNS, dispatcher -> new RenderTestLiftHallLanterns(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_BUTTONS_WITHOUT_SCREEN, RenderTestLiftButtonsWithoutScreen::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_PANEL, dispatcher -> new RenderTestLiftPanel(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_1, RenderMitsubishiNexWayButton1::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.MITSUBISHI_NEXWAY_BUTTON_2, RenderMitsubishiNexWayButton2::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_BUTTON_1, RenderOtisSeries1Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_BUTTON_2, RenderOtisSeries1Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.OTIS_SERIES_1_SCREEN_1, dispatcher -> new RenderOtisSeries1Screen(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_D_SERIES_D2BUTTON, RenderSchindlerDSeriesD2Button::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_BUTTON, RenderSchindlerMSeriesButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_TOUCH_BUTTON, RenderSchindlerMSeriesTouchButton::new);
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_1, dispatcher -> new RenderSchindlerMSeriesScreen1(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_2_ODD, dispatcher -> new RenderSchindlerMSeriesScreen2<>(dispatcher, true));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_SCREEN_2_EVEN, dispatcher -> new RenderSchindlerMSeriesScreen2<>(dispatcher, false));
        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.SCHINDLER_M_SERIES_BUTTON, RenderSchindlerMSeriesButton::new);


        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());
        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_LIFT_BUTTONS_LINK_REMOVER, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());
        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_GROUP_LIFT_BUTTONS_LINK_CONNECTOR, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());
        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_GROUP_LIFT_BUTTONS_LINK_REMOVER, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());

        REGISTRY_CLIENT.setupPackets(new Identifier(Init.MOD_ID, "packet"));

        REGISTRY_CLIENT.eventRegistryClient.registerClientJoin(() -> {
            MinecraftClientData.reset();
            TextureCache.instance = new TextureCache();
            lastMillis = System.currentTimeMillis();
            gameMillis = 0;

            TextureCache.instance.reload();

        });
        REGISTRY_CLIENT.init();
    }


    private static RegistryClient.ModelPredicateProvider checkItemPredicateTag() {
        return (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(ItemBlockClickingBase.TAG_POS) ? 1 : 0;
    }

    public static float getGameTick() {
        return (float) gameMillis / 50.0F;
    }
}