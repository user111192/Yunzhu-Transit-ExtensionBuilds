package top.xfunny.util;

import org.mtr.mapping.holder.Direction;

public class TransformPositionX {
    public static double transform(double x,double z, Direction facing){
        return switch (facing) {
            case NORTH -> x;
            case SOUTH -> 1-x;
            case EAST -> z;
            case WEST -> 1-z;
            default -> 0;
        };
    }
}
