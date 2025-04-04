package top.xfunny.mod.data;

import org.mtr.mapping.holder.BooleanProperty;
import org.mtr.mapping.holder.DirectionProperty;
import org.mtr.mapping.holder.IntegerProperty;
import org.mtr.mapping.mapper.DirectionHelper;

/**
 * Stores all block properties JCM uses. Block classes from JCM should reference the block properties in here
 */
public interface BlockProperties {
    public static final DirectionProperty FACING = DirectionHelper.FACING;
    public static final IntegerProperty BARRIER_FENCE_TYPE = IntegerProperty.of("type", 0, 10);
    public static final BooleanProperty BARRIER_FLIPPED = BooleanProperty.of("flipped");
    public static final BooleanProperty HORIZONTAL_IS_LEFT = BooleanProperty.of("left");
}
