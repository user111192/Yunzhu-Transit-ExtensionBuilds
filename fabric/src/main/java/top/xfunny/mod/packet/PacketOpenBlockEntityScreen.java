package top.xfunny.mod.packet;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import top.xfunny.mod.client.client_packet.ClientPacketHelper;

public final class PacketOpenBlockEntityScreen extends PacketHandler {

    private final BlockPos blockPos;

    public PacketOpenBlockEntityScreen(PacketBufferReceiver packetBufferReceiver) {
        blockPos = BlockPos.fromLong(packetBufferReceiver.readLong());
    }

    public PacketOpenBlockEntityScreen(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeLong(blockPos.asLong());
    }

    @Override
    public void runClient() {
        System.out.println("PacketOpenBlockEntityScreen");
        ClientPacketHelper.openBlockEntityScreen(blockPos);
    }
}
