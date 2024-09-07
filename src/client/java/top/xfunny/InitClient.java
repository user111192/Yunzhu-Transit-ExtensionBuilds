package top.xfunny;

import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.registry.RegistryClient;



import org.mtr.mod.render.RenderLiftPanel;
import top.xfunny.Render.RenderTestLiftButtons;
import top.xfunny.Render.RenderTestLiftButtons2;


public final class InitClient {
    public static final RegistryClient REGISTRY_CLIENT = new RegistryClient(Init.REGISTRY);

    public static void init() {


        REGISTRY_CLIENT.registerBlockEntityRenderer(BlockEntityTypes.TEST_LIFT_BUTTONS, RenderTestLiftButtons2::new);








        REGISTRY_CLIENT.init();
    }


}
