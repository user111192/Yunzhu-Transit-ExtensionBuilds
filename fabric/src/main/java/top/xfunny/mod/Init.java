package top.xfunny.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mtr.core.data.Position;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MathHelper;
import org.mtr.mapping.registry.Registry;
import top.xfunny.mod.packet.PacketOpenBlockEntityScreen;
import top.xfunny.mod.packet.PacketUpdatePATRS01RailwaySignConfig;

public final class Init {
    public static final String MOD_ID = "yte";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Registry REGISTRY = new Registry();


    public static void init() {
        Blocks.init();
        BlockEntityTypes.init();
        Items.init();
        CreativeModeTabs.init();


        //注册mtr packet
        REGISTRY.setupPackets(new Identifier(MOD_ID, "packet"));
        REGISTRY.registerPacket(PacketOpenBlockEntityScreen.class, PacketOpenBlockEntityScreen::new);
        REGISTRY.registerPacket(PacketUpdatePATRS01RailwaySignConfig.class, PacketUpdatePATRS01RailwaySignConfig::new);

        REGISTRY.init();
    }

    public static Position blockPosToPosition(BlockPos blockPos) {
        return new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static BlockPos positionToBlockPos(Position position) {
        return new BlockPos((int) position.getX(), (int) position.getY(), (int) position.getZ());
    }

    public static BlockPos newBlockPos(double x, double y, double z) {
        return new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }
}

