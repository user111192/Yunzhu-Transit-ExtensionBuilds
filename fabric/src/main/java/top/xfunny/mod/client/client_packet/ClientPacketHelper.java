package top.xfunny.mod.client.client_packet;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ScreenExtension;
import top.xfunny.mod.block.EmptyFloor;
import top.xfunny.mod.client.screen.LiftEmptyFloorScreen;

import java.util.function.Consumer;
import java.util.function.Predicate;

//MTR ClientPacketHelper
public final class ClientPacketHelper {

    public static void openBlockEntityScreen(BlockPos blockPos) {
        getBlockEntity(blockPos, blockEntity -> {
            if (blockEntity.data instanceof EmptyFloor.BlockEntity) {
                openScreen(new LiftEmptyFloorScreen(blockPos, (EmptyFloor.BlockEntity) blockEntity.data), screenExtension -> screenExtension instanceof LiftEmptyFloorScreen);
            }
        });
    }

    private static void openScreen(ScreenExtension screenExtension, Predicate<ScreenExtension> isInstance) {
        final MinecraftClient minecraftClient = MinecraftClient.getInstance();
        final Screen screen = minecraftClient.getCurrentScreenMapped();
        if (screen == null || screen.data instanceof ScreenExtension && !isInstance.test((ScreenExtension) screen.data)) {
            minecraftClient.openScreen(new Screen(screenExtension));
        }
    }

    private static void getBlockEntity(BlockPos blockPos, Consumer<BlockEntity> consumer) {
        final ClientWorld clientWorld = MinecraftClient.getInstance().getWorldMapped();
        if (clientWorld != null) {
            final BlockEntity blockEntity = clientWorld.getBlockEntity(blockPos);
            if (blockEntity != null) {
                consumer.accept(blockEntity);
            }
        }
    }
}
