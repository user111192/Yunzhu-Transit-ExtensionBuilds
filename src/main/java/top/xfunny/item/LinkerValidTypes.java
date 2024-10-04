package top.xfunny.item;

import org.mtr.mod.block.BlockLiftTrackFloor;
import top.xfunny.block.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LinkerValidTypes {
    public static final Set<Class<?>> VALID_TYPES = new HashSet<>(Arrays.asList(
            BlockLiftTrackFloor.class,
            TestLiftButtons.class,
            TestLiftButtonsWithoutScreen.class,
            OtisSeries1Button.class,
            OtisSeries1Screen.class,
            TestLiftHallLanterns.class,
            TestLiftPanel.class,
            SchindlerDSeriesD2Button.class,
            SchindlerMSeriesPushButton.class
    ));
}
