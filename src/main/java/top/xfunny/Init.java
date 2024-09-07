package top.xfunny;

import org.mtr.mapping.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Init {
    	public static final String MOD_ID = "yte";
	    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	    public static final Registry REGISTRY = new Registry();
		public static void init()
		{
			Blocks.init();
			BlockEntityTypes.init();
			Items.init();

			REGISTRY.init();


		}










}

