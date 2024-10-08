package top.xfunny.item;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftFloor;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mod.block.*;
import org.mtr.mod.generated.lang.TranslationProvider;
import org.mtr.mod.item.ItemBlockClickingBase;
import top.xfunny.Init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloorAutoSetter extends ItemExtension implements DirectionHelper {

    ArrayList<BlockPos> mark;
    Object[] array;
    BlockPos pos;
    int number;


    public FloorAutoSetter(ItemSettings itemSettings) {
        super(itemSettings.maxCount(1));
    }
    @Nonnull
    public ActionResult useOnBlock2(ItemUsageContext context) {
        if (!context.getWorld().isClient()){
            if (this.clickCondition(context)) {
                CompoundTag compoundTag = context.getStack().getOrCreateTag();
                compoundTag.putLong("pos", context.getBlockPos().asLong());
                this.onClick(context, compoundTag);
                return ActionResult.SUCCESS;
            }else{
                return ActionResult.FAIL;
            }
        }else {
            return super.useOnBlock2(context);
        }
    }


    protected void onClick(ItemUsageContext context,CompoundTag compoundTag) {
        final World world = context.getWorld();
        pos = context.getBlockPos();
        array = new Object[2];
        array[1] = null; // 下一个轨道pos坐标
        mark =new ArrayList<>();
        Boolean ding = false;
        number = 0;
        int floorNumber = 0;
        String floorNumber2 = "";
        final BlockEntity floorEntity = world.getBlockEntity(pos);
        if (floorEntity != null && floorEntity.data instanceof BlockLiftTrackFloor.BlockEntity) {
            String checkfloorNumber = ((BlockLiftTrackFloor.BlockEntity) floorEntity.data).getFloorNumber();
            if (checkfloorNumber != null){
                floorNumber2 = ((BlockLiftTrackFloor.BlockEntity) floorEntity.data).getFloorNumber();
            }else{
                floorNumber2 = "1";
            }
            ding = ((BlockLiftTrackFloor.BlockEntity) floorEntity.data).getShouldDing();
        } else {
            Init.LOGGER.info("没有找到有效轨道");
        }
        if (floorNumber2.matches("\\d+")) {
            floorNumber = Integer.parseInt(floorNumber2);
            Init.LOGGER.info("楼层：" + floorNumber);
        } else {
            Init.LOGGER.info("楼层必须是整数");
        }



        while (floorNumber2.matches("\\d+")) {
            Init.LOGGER.info("-----------------------------------------------------------------------------------");
            if (world.getBlockState(pos).getBlock().data instanceof BlockLiftTrackBase) {
                final BlockEntity floorEntity1 = world.getBlockEntity(pos);
                if (floorEntity1 != null && floorEntity1.data instanceof BlockLiftTrackFloor.BlockEntity) {
                    ((BlockLiftTrackFloor.BlockEntity) floorEntity1.data).setData(String.valueOf(floorNumber), "", ding);
                    Init.LOGGER.info("设置楼层：" + floorNumber);
                }

                Object[] apos = check(context, pos);
                pos = (BlockPos) apos[1];
                // 检查返回值是否有效
                if (pos == null) {
                    Init.LOGGER.info("Both positions are null, exiting loop.");
                    break; // 退出循环
                }
            } else {
                Init.LOGGER.info("没有找到有效轨道，退出循环。");
                break; // 退出循环
            }
            if (number == mark.size()){
                Init.LOGGER.info("mark.size() == number");
                break;
            }
            number++;
            if(world.getBlockState(pos.up(1)).getBlock().data instanceof BlockLiftTrackFloor){
                floorNumber++;
            }

        }
    }
    private Object[] check(ItemUsageContext context, BlockPos pos) {
        final World world = context.getWorld();
        Init.LOGGER.info("checkpos1" + pos.toShortString());
        Init.LOGGER.info("checkpos0" + array[0]);
        checkPosition(world, pos, facingHelper(context,pos));
        Init.LOGGER.info("checkpos3" + Arrays.toString(array));
        return array;
    }

    private void checkPosition(World world, BlockPos pos, boolean facing) {
        Init.LOGGER.info("checkPosition");
        if (world.getBlockState(pos.down(1)).getBlock().data instanceof BlockLiftTrackBase) {
            Init.LOGGER.info("下");
            if (!findMark(pos.down(1))) {
                Init.LOGGER.info((facing ? "东西向" : "南北向") + "判断-下");
                array[1] = pos.down(1);

                mark.add(pos);
            }
            else {
                Init.LOGGER.info("重复位置，跳过");
            }
        }
        if (world.getBlockState(pos.up(1)).getBlock().data instanceof BlockLiftTrackBase) {
            Init.LOGGER.info("上");
            if (!findMark(pos.up(1))) {
                Init.LOGGER.info((facing ? "东西向" : "南北向") + "判断-上");
                array[1] = pos.up(1);
                mark.add(pos);
            }
            else {
                Init.LOGGER.info("重复位置，跳过");
            }
        } if (world.getBlockState(pos.south(1)).getBlock().data instanceof BlockLiftTrackBase&& facing) {
            Init.LOGGER.info("南");
            if (!findMark(pos.south(1))) {
                Init.LOGGER.info((facing ? "东西向" : "南北向") + "判断-南");
                array[1] = pos.south(1);

                mark.add(pos);
            }
            else {
                Init.LOGGER.info("重复位置，跳过");
            }
        } if (world.getBlockState(pos.north(1)).getBlock().data instanceof BlockLiftTrackBase&& facing) {
            Init.LOGGER.info("北");
            if (!findMark(pos.north(1))) {
                Init.LOGGER.info((facing ? "东西向" : "南北向") + "判断-北");
                array[1] = pos.north(1);

                mark.add(pos);
            }
            else {
                Init.LOGGER.info("重复位置，跳过");
            }
        }  if (world.getBlockState(pos.east(1)).getBlock().data instanceof BlockLiftTrackBase&&!facing) {
            Init.LOGGER.info("东");
            if (!findMark(pos.east(1))) {
                Init.LOGGER.info((facing ? "东西向" : "南北向") + "判断-下");
                array[1] = pos.east(1);

                mark.add(pos);
            }
            else {
                Init.LOGGER.info("重复位置，跳过");
            }
        } if (world.getBlockState(pos.west(1)).getBlock().data instanceof BlockLiftTrackBase&&!facing) {
            Init.LOGGER.info("西");
            if (!findMark(pos.west(1))) {
                Init.LOGGER.info((facing ? "东西向" : "南北向") + "判断-下");
                array[1] = pos.west(1);

                mark.add(pos);
            }
            else {
                Init.LOGGER.info("重复位置，跳过");
            }
        }
        else {
            Init.LOGGER.info((facing ? "东西向" : "南北向") + "判断失败");
        }
        Init.LOGGER.info("checkposition结束");
    }

    public Boolean findMark(BlockPos pos) {
        for (int i = 0; i < mark.size(); i++) {
            if (mark.get(i).equals(pos)) {
                Init.LOGGER.info("找到重复位置，退出循环。");
                return true;
            }
        }
        Init.LOGGER.info("没有找到重复位置，继续循环。");
        return false;
    }

    protected boolean clickCondition(ItemUsageContext context) {
        final Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
        return block.data instanceof BlockLiftTrackFloor;
    }

    private boolean facingHelper(ItemUsageContext context,BlockPos pos){
        final World world = context.getWorld();
        if(world.getBlockState(pos).getBlock().data instanceof BlockLiftTrackBase){
            if(IBlock.getStatePropertySafe(world.getBlockState(pos), FACING)==Direction.EAST||IBlock.getStatePropertySafe(world.getBlockState(pos), FACING)==Direction.WEST){
                return true;//东西
            }else{
                return false;//南北
            }
        }
        return false;
    }
}
