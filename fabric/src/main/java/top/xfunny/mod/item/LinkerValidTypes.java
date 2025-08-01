package top.xfunny.mod.item;

import org.mtr.mod.block.BlockLiftTrackFloor;
import top.xfunny.mod.block.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LinkerValidTypes {
    public static final Set<Class<?>> VALID_TYPES = new HashSet<>(Arrays.asList(
            org.mtr.mod.block.BlockLiftButtons.class,
            org.mtr.mod.block.BlockLiftPanelEven1.class,
            org.mtr.mod.block.BlockLiftPanelEven2.class,
            org.mtr.mod.block.BlockLiftPanelOdd1.class,
            org.mtr.mod.block.BlockLiftPanelOdd2.class,


            CESScreen1Odd.class,
            CESScreen1Even.class,
            DewhurstUS89Button1.class,
            DewhurstUS91Button1.class,
            DewhurstUS91Button1Braille.class,
            BlockLiftTrackFloor.class,
            TestLiftButtons.class,
            TestLiftButtonsWithoutScreen.class,
            TestLiftDestinationDispatchTerminal.class,
            OtisSeries1Button.class,
            OtisSeries1Screen.class,
            OtisSeries3Button1.class,
            OtisSeries3Screen1Odd.class,
            OtisSeries3Screen1Even.class,
            OtisSeries3ELDScreen1Odd.class,
            OtisSeries3ELDScreen1Even.class,
            OtisSeries3Lantern1ArrowOdd.class,
            OtisSeries3Lantern1ArrowEven.class,
            OtisSPEC60Button1.class,
            TestLiftHallLanterns.class,
            TestLiftPanel.class,
            HitachiVIB320Button.class,
            HitachiVIB320ButtonDotMatrix.class,
            HitachiVIB322Button.class,
            HitachiVIB322ButtonDotMatrix.class,
            HitachiVIB221Button.class,
            HitachiVIB221ButtonDotMatrix.class,
            HitachiVIB820Button.class,
            HitachiVIB820ButtonLCD.class,
            HitachiVIB668Button.class,
            HitachiButtonPAFC.class,
            KoneKDS330Button1.class,
            KoneKDS330Button1WithoutScreen.class,
            KoneKDS330Lantern1.class,
            KoneKSS280Button1.class,
            KoneKSS280Button1WithoutScreen.class,
            KoneKSS280Screen1Odd.class,
            KoneKSS280Screen1Even.class,
            KoneMButton1.class,
            KoneMButton2.class,
            KoneMScreen1Odd.class,
            KoneMScreen1Even.class,
            MitsubishiGPSButton1.class,
            MitsubishiGPSButton1WithoutScreen.class,
            MitsubishiButtonShunHingPlaza.class,
            MitsubishiRyodenScreen1Even.class,
            MitsubishiRyodenScreen1Odd.class,
            MitsubishiNexWayButton1.class,
            MitsubishiNexWayButton1Segmented.class,
            MitsubishiNexWayButton1WithoutScreen.class,
            MitsubishiNexWayButton2.class,
            MitsubishiNexWayButton2Segmented.class,
            MitsubishiNexWayButton2LCD1.class,
            MitsubishiNexWayButton2WithoutScreen.class,
            MitsubishiNexWayButton3.class,
            MitsubishiNexWayButton3Segmented.class,
            MitsubishiNexWayButton3WithoutScreen.class,
            MitsubishiNexWayButton4.class,
            MitsubishiNexWayButton4WithoutScreen.class,
            MitsubishiMaxiezButton1Gold.class,
            MitsubishiMaxiezButton1Silver.class,
            MitsubishiMPVFButton1.class,
            MitsubishiMPVFScreen1VerticalOdd.class,
            MitsubishiMPVFScreen1VerticalEven.class,
            MitsubishiNexWayLantern1HorizontalOdd.class,
            MitsubishiNexWayLantern1HorizontalEven.class,
            MitsubishiNexWayLantern1VerticalOdd.class,
            MitsubishiNexWayLantern1VerticalEven.class,
            MitsubishiNexWayScreen1Even.class,
            MitsubishiNexWayScreen1Odd.class,
            MitsubishiNexWayScreen1SegmentedEven.class,
            MitsubishiNexWayScreen1SegmentedOdd.class,
            MitsubishiNexWayScreen2Even.class,
            MitsubishiNexWayScreen2Odd.class,
            MitsubishiNexWayScreen3Even.class,
            MitsubishiNexWayScreen3Odd.class,
            MitsubishiNexWayScreen3SegmentedEven.class,
            MitsubishiNexWayScreen3SegmentedOdd.class,
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
            SchindlerMSeriesScreen4Odd.class,
            SchindlerMSeriesScreen4Even.class,
            SchindlerSSeriesGreyButton.class,
            SchindlerLineaButton1White.class,
            SchindlerLineaButton1Black.class,
            SchindlerLineaButton2White.class,
            SchindlerLineaButton2Black.class,
            HitachiB85Button1.class,
            HitachiB85Button1WithoutScreen.class,
            HitachiB85Button2.class,
            SchindlerZLine3Keypad1.class,
            ThyssenkruppTEGL1Button1.class,
            TonicDSScreen1Odd.class,
            TonicDSScreen1Even.class,
            TonicDMScreen1RedOdd.class,
            TonicDMScreen1RedEven.class,
            TonicDMScreen1GreenOdd.class,
            TonicDMScreen1GreenEven.class,
            TonicDMScreen1YellowOdd.class,
            TonicDMScreen1YellowEven.class,
            SchindlerZLine3Keypad1.class
    ));
}
