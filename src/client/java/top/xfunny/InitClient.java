package top.xfunny;

import org.mtr.mapping.holder.Identifier;

import org.mtr.mapping.registry.RegistryClient;



import org.mtr.mod.item.ItemBlockClickingBase;

import top.xfunny.Render.RenderTestLiftButtons2;


public final class InitClient {
    public static final RegistryClient REGISTRY_CLIENT = new RegistryClient(Init.REGISTRY);

    public static void init() {


        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_BUTTONS, RenderTestLiftButtons2::new);

        REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_LIFT_BUTTONS_LINK_CONNECTOR, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());
		REGISTRY_CLIENT.registerItemModelPredicate(Items.YTE_LIFT_BUTTONS_LINK_REMOVER, new Identifier(org.mtr.mod.Init.MOD_ID, "selected"), checkItemPredicateTag());









        REGISTRY_CLIENT.init();
    }

    	private static RegistryClient.ModelPredicateProvider checkItemPredicateTag() {
		return (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(ItemBlockClickingBase.TAG_POS) ? 1 : 0;
	}


}
