package top.xfunny.mod.item;

import org.mtr.mod.block.BlockLiftTrackFloor;
import top.xfunny.mod.block.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LinkerValidTypes {
    public static final Set<Class<?>> VALID_TYPES = new HashSet<>(Arrays.asList(
            CESScreen1Odd.class,
            CESScreen1Even.class,
            BlockLiftTrackFloor.class,
            TestLiftButtons.class,
            TestLiftButtonsWithoutScreen.class,
            TestLiftDestinationDispatchTerminal.class,
            OtisSeries1Button.class,
            OtisSeries1Screen.class,
            TestLiftHallLanterns.class,
            TestLiftPanel.class,
            KoneMButton1.class,
            KoneMButton2.class,
            KoneMScreen1Odd.class,
            KoneMScreen1Even.class,
            MitsubishiNexWayButton1.class,
            MitsubishiNexWayButton1WithoutScreen.class,
            MitsubishiNexWayButton2.class,
            MitsubishiNexWayButton2WithoutScreen.class,
            MitsubishiNexWayButton3.class,
            MitsubishiNexWayButton3WithoutScreen.class,
            MitsubishiNexWayScreen1Even.class,
            MitsubishiNexWayScreen1Odd.class,
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
            SchindlerMSeriesRoundTouchButton.class,
            SchindlerMSeriesRoundLantern1Odd.class,
            SchindlerMSeriesRoundLantern1Even.class,
            SchindlerMSeriesScreen1.class,
            SchindlerMSeriesScreen2Odd.class,
            SchindlerMSeriesScreen2Even.class,
            SchindlerMSeriesScreen3Odd.class,
            SchindlerMSeriesScreen3Even.class,
            SchindlerSSeriesGreyButton.class,
            HitachiB85Button1.class,
            HitachiB85Button1WithoutScreen.class,
            HitachiB85Button2.class,
            SchindlerZLine3Keypad1.class
    ));
}
