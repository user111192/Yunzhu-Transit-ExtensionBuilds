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
            MitsubishiNexWayButton3.class,
            SchindlerDSeriesD2Button.class,
            SchindlerDSeriesScreen1Even.class,
            SchindlerDSeriesScreen1Odd.class,
            SchindlerDSeriesScreen2GreenEven.class,
            SchindlerDSeriesScreen2GreenOdd.class,
            SchindlerDSeriesScreen2RedEven.class,
            SchindlerDSeriesScreen2RedOdd.class,
            SchindlerDSeriesScreen2BlueEven.class,
            SchindlerDSeriesScreen2BlueOdd.class,
            SchindlerMSeriesButton.class,
            SchindlerMSeriesTouchButton.class,
            SchindlerMSeriesScreen1.class,
            SchindlerMSeriesScreen2Odd.class,
            SchindlerMSeriesScreen2Even.class,
            SchindlerSSeriesGreyButton.class,
            HitachiB85Button1.class

    ));
}
