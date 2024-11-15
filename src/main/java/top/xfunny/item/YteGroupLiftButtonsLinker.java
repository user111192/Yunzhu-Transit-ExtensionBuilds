package top.xfunny.item;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mod.block.BlockLiftTrackBase;
import org.mtr.mod.block.BlockLiftTrackFloor;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.item.ItemBlockClickingBase;
import top.xfunny.ButtonRegistry;
import top.xfunny.Init;
import top.xfunny.LiftFloorRegistry;
import top.xfunny.block.base.LiftButtonsBase;
import top.xfunny.block.base.LiftHallLanternsBase;

import java.util.ArrayList;
import java.util.Arrays;

import static top.xfunny.item.LinkerValidTypes.VALID_TYPES;

public class YteGroupLiftButtonsLinker extends ItemBlockClickingBase implements DirectionHelper {
    private final boolean isConnector;
    ArrayList<BlockPos> mark;
    Object[] array;
    BlockPos posStart;
    int number;

    public YteGroupLiftButtonsLinker(boolean isConnector, ItemSettings itemSettings) {
        super(itemSettings.maxCount(1));
        this.isConnector = isConnector;
    }

    @Override
    protected void onStartClick(ItemUsageContext context, CompoundTag compoundTag) {
        Init.LOGGER.info("Clicked lift buttons linker");
    }

    @Override
    protected void onEndClick(ItemUsageContext context, BlockPos posEnd, CompoundTag compoundTag) {
        final World world = context.getWorld();
        posStart = context.getBlockPos();
        array = new Object[3];
        array[1] = null; // 下一个轨道pos1坐标
        array[2] = null; // 下一个轨道pos2坐标
        mark =new ArrayList<>();
        number = 0;


        while (true) {
            Init.LOGGER.info("-----------------------------------------------------------------------------------");
            if (world.getBlockState(posStart).getBlock().data instanceof BlockLiftTrackBase ||
                    world.getBlockState(posEnd).getBlock().data instanceof BlockLiftTrackBase) {
                connect(world, posStart, posEnd, isConnector);
                connect(world, posEnd, posStart, isConnector);
                Object[] pos = check(context, posStart, posEnd);
                posStart = (BlockPos) pos[1];
                posEnd = (BlockPos) pos[2];
                // 检查返回值是否有效
                if (posStart == null && posEnd == null) {
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
        }
    }


    private Object[] check(ItemUsageContext context, BlockPos pos1, BlockPos pos2) {
        final World world = context.getWorld();
        Init.LOGGER.info("checkpos1" + pos1.toShortString());
        Init.LOGGER.info("checkpos2" + pos2.toShortString());
        Init.LOGGER.info("checkpos0" + array[0]);


        if(world.getBlockState(pos1).getBlock().data instanceof BlockLiftTrackBase){
            checkPosition(world, pos1,pos2, facingHelper(context,pos1, pos2));
        }else{
            checkPosition(world, pos2,pos1, facingHelper(context,pos1, pos2));
        }


        Init.LOGGER.info("checkpos3" + Arrays.toString(array));
        return array;
    }

    private void checkPosition(World world, BlockPos pos,BlockPos otherPos, boolean facing) {
        Init.LOGGER.info("checkPosition");


        if (world.getBlockState(pos.up(1)).getBlock().data instanceof BlockLiftTrackBase) {
            Init.LOGGER.info("上");
            if (!findMark(pos.up(1))) {
                Init.LOGGER.info((facing ? "东西向" : "南北向") + "判断-上");
                array[1] = pos.up(1);
                array[2] = otherPos.up(1);
                mark.add(pos);
            }
            else {
                Init.LOGGER.info("重复位置，跳过");
            }
        }
        if (world.getBlockState(pos.down(1)).getBlock().data instanceof BlockLiftTrackBase) {
            Init.LOGGER.info("下");
            if (!findMark(pos.down(1))) {
                Init.LOGGER.info((facing ? "东西向" : "南北向") + "判断-下");
                array[1] = pos.down(1);
                array[2] = otherPos.down(1);
                mark.add(pos);
            }
            else {
                Init.LOGGER.info("重复位置，跳过");
            }
        }if (world.getBlockState(pos.south(1)).getBlock().data instanceof BlockLiftTrackBase&& facing) {
            Init.LOGGER.info("南");
            if (!findMark(pos.south(1))) {
                Init.LOGGER.info((facing ? "东西向" : "南北向") + "判断-南");
                array[1] = pos.south(1);
                array[2] = otherPos.south(1);
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
                array[2] = otherPos.north(1);
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
                array[2] = otherPos.east(1);
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
                array[2] = otherPos.west(1);
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



    private boolean isValidType(Object data) {
        return VALID_TYPES.contains(data.getClass());
    }

    @Override
    protected boolean clickCondition(ItemUsageContext context) {
        final Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
        return isValidType(block.data);
    }

    private boolean facingHelper(ItemUsageContext context,BlockPos pos1, BlockPos pos2){
        final World world = context.getWorld();
        if(world.getBlockState(pos1).getBlock().data instanceof BlockLiftTrackBase){
            if(IBlock.getStatePropertySafe(world.getBlockState(pos1), FACING)==Direction.EAST||IBlock.getStatePropertySafe(world.getBlockState(pos1), FACING)==Direction.WEST){
                return true;//东西
            }else{
                return false;//南北
            }

        } else if(world.getBlockState(pos2).getBlock().data instanceof BlockLiftTrackBase) {
            if(IBlock.getStatePropertySafe(world.getBlockState(pos2), FACING)==Direction.EAST||IBlock.getStatePropertySafe(world.getBlockState(pos2), FACING)==Direction.WEST){
                return true;//东西
            }else{
                return false;//南北
            }
        }
        return false;
    }

   	private static void connect(World world, BlockPos blockPos1, BlockPos blockPos2, boolean isAdd) {
		final BlockEntity blockEntity1 = world.getBlockEntity(blockPos1);
		final BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);

		// 合并日志输出，减少信息泄露风险
		if (blockEntity1 != null && blockEntity2 != null) {
			Init.LOGGER.info("正在尝试连接 {} 和 {}", blockPos1, blockPos2);

			// 简化类型检查
			if (blockEntity2.data instanceof BlockLiftTrackFloor.BlockEntity) {
				if (blockEntity1.data instanceof LiftFloorRegistry) {
					((LiftFloorRegistry) blockEntity1.data).registerFloor(blockPos1, world, blockPos2, isAdd);
					Init.LOGGER.info("已成功连接 {} 和 {}", blockPos1, blockPos2);
				} else {
					Init.LOGGER.info("未能连接 {} 和 {}", blockPos1, blockPos2);
				}
			} else if(blockEntity2.data instanceof LiftButtonsBase.BlockEntityBase){
				if (blockEntity1.data instanceof LiftHallLanternsBase.BlockEntityBase) {
					((ButtonRegistry) blockEntity2.data).registerButton(world, blockPos1, isAdd);
					((ButtonRegistry) blockEntity1.data).registerButton(world, blockPos2, isAdd);
					Init.LOGGER.info("到站灯已成功连接 {} 和 {}", blockPos1.toShortString(), blockPos2.toShortString());
				} else {
					Init.LOGGER.info("到站灯未能连接 {} 和 {}", blockPos1, blockPos2);
				}
			}else{
				Init.LOGGER.info("未能连接 {} 和 {}", blockPos1, blockPos2);
			}
		} else {
			Init.LOGGER.warn("BlockEntity 为空，无法连接 {} 和 {}", blockPos1, blockPos2);
		}
	}
}