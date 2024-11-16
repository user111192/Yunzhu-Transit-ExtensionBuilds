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
            MitsubishiNexWayButton1.class,
            MitsubishiNexWayButton2.class,
            SchindlerDSeriesD2Button.class,
            SchindlerMSeriesButton.class,
            SchindlerMSeriesTouchButton.class,
            SchindlerMSeriesScreen1.class,
            SchindlerMSeriesScreen2Odd.class,
            SchindlerMSeriesScreen2Even.class
    ));
}
