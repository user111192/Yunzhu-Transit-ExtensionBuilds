package top.xfunny;

import org.mtr.core.servlet.Webserver;
import org.mtr.libraries.org.eclipse.jetty.servlet.ServletHolder;
import org.mtr.mapping.holder.Identifier;

import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.registry.RegistryClient;


import org.mtr.mod.QrCodeHelper;
import org.mtr.mod.client.DynamicTextureCache;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.item.ItemBlockClickingBase;

import org.mtr.mod.servlet.ClientServlet;
import org.mtr.mod.servlet.Tunnel;
import top.xfunny.Render.RenderTestLiftButtons2;


public final class InitClient {
    public static final RegistryClient REGISTRY_CLIENT = new RegistryClient(Init.REGISTRY);
    private static long lastMillis = 0;
	private static long gameMillis = 0;

    public static void init() {


        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_BUTTONS, RenderTestLiftButtons2::new);

        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());
		REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_LIFT_BUTTONS_LINK_REMOVER, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());



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


}
