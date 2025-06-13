package top.xfunny.mixin;

import org.mtr.mapping.holder.*;
import org.mtr.mod.Init;
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import org.mtr.mod.block.PlatformHelper;
import org.mtr.mod.render.PositionAndRotation;
import org.mtr.mod.render.RenderVehicleHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.xfunny.mod.block.base.OldBlockPSDAPGDoorBase;

@Mixin(value = RenderVehicleHelper.class, remap = false)
public class MixinRenderVehicleHelper {
    @Inject(method = "canOpenDoors", at = @At("HEAD"), cancellable = true)
    private static void injectCanOpenDoors(Box doorway, PositionAndRotation positionAndRotation, double doorValue, CallbackInfoReturnable<Boolean> cir) {
        final ClientWorld clientWorld = MinecraftClient.getInstance().getWorldMapped();
        if (clientWorld == null) {
            cir.setReturnValue(false);
            return;
        }

        final Vector3d[] doorwayPositions = new Vector3d[]{
                positionAndRotation.transformForwards(new Vector3d(doorway.getMinXMapped(), doorway.getMaxYMapped(), doorway.getMinZMapped()), Vector3d::rotateX, Vector3d::rotateY, Vector3d::add),
                positionAndRotation.transformForwards(new Vector3d(doorway.getMaxXMapped(), doorway.getMaxYMapped(), doorway.getMinZMapped()), Vector3d::rotateX, Vector3d::rotateY, Vector3d::add),
                positionAndRotation.transformForwards(new Vector3d(doorway.getMaxXMapped(), doorway.getMaxYMapped(), doorway.getMaxZMapped()), Vector3d::rotateX, Vector3d::rotateY, Vector3d::add),
                positionAndRotation.transformForwards(new Vector3d(doorway.getMinXMapped(), doorway.getMaxYMapped(), doorway.getMaxZMapped()), Vector3d::rotateX, Vector3d::rotateY, Vector3d::add)
        };

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        for (Vector3d pos : doorwayPositions) {
            minX = Math.min(minX, pos.getXMapped());
            maxX = Math.max(maxX, pos.getXMapped());
            minY = Math.min(minY, pos.getYMapped());
            maxY = Math.max(maxY, pos.getYMapped());
            minZ = Math.min(minZ, pos.getZMapped());
            maxZ = Math.max(maxZ, pos.getZMapped());
        }

        boolean canOpenDoors = false;

        for (double checkX = minX - 1; checkX <= maxX + 1; checkX++) {
            for (double checkY = minY - 2; checkY <= maxY + 2; checkY++) {
                for (double checkZ = minZ - 1; checkZ <= maxZ + 1; checkZ++) {
                    final BlockPos checkPos = Init.newBlockPos(checkX, checkY, checkZ);
                    final BlockState blockState = clientWorld.getBlockState(checkPos);
                    final Block block = blockState.getBlock();

                    if (block.data instanceof PlatformHelper) {
                        canOpenDoors = true;
                    } else if (
                            (block.data instanceof BlockPSDAPGDoorBase && blockState.get(new Property<>(BlockPSDAPGDoorBase.UNLOCKED.data))) ||
                                    (block.data instanceof OldBlockPSDAPGDoorBase && blockState.get(new Property<>(OldBlockPSDAPGDoorBase.UNLOCKED.data)))
                    ) {
                        canOpenDoors = true;
                        final BlockEntity blockEntity = clientWorld.getBlockEntity(checkPos);
                        if (blockEntity != null) {
                            if (blockEntity.data instanceof BlockPSDAPGDoorBase.BlockEntityBase entity) {
                                entity.setDoorValue(doorValue);
                            } else if (blockEntity.data instanceof OldBlockPSDAPGDoorBase.BlockEntityBase oldEntity) {
                                oldEntity.open(doorValue);
                            }
                        }
                    }
                }
            }
        }

        cir.setReturnValue(canOpenDoors);
    }
}


