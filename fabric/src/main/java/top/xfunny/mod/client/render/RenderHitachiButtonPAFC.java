package top.xfunny.mod.client.render;

import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.IGui;
import top.xfunny.mod.block.HitachiButton_PAFC;

public class RenderHitachiButtonPAFC extends BlockEntityRenderer<HitachiButton_PAFC.BlockEntity> implements DirectionHelper, IGui, IBlock {

    public RenderHitachiButtonPAFC(Argument dispatcher) {super(dispatcher);}
    @Override
    public void render(HitachiButton_PAFC.BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder1, int light, int overlay) {
        final World world = blockEntity.getWorld2();
        if (world == null) {
            return;
        }

    }
}
