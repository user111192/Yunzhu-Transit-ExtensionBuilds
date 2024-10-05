package top.xfunny;

import net.fabricmc.api.ClientModInitializer;
import org.mtr.core.data.LiftDirection;
import top.xfunny.block.base.LiftButtonsBase;

import java.util.LinkedList;
import java.util.Queue;

public class YunzhuTransitExtensionClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		InitClient.init();



	}
}