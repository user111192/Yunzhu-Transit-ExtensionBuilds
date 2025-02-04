package top.xfunny.keymapping;


public class TestLiftDestinationDispatchTerminalKeyMapping {
    public String mapping(String screenId, double hitX, double hitY) {
        switch (screenId) {
            case "test_lift_destination_dispatch_terminal_key_mapping_home":
                if (hitX > 0.0625 * 1 && hitX < 0.0625 * 2 && hitY > 0.375 && hitY < 0.4375) {
                    return "number1";
                }
                if (hitX > 0.0625 * 3 && hitX < 0.0625 * 4 && hitY > 0.375 && hitY < 0.4375) {
                    return "number2";
                }
                if (hitX > 0.0625 * 5 && hitX < 0.0625 * 6 && hitY > 0.375 && hitY < 0.4375) {
                    return "number3";
                }
                if (hitX > 0.0625 * 1 && hitX < 0.0625 * 2 && hitY > 0.25 && hitY < 0.3125) {
                    return "number4";
                }
                if (hitX > 0.0625 * 3 && hitX < 0.0625 * 4 && hitY > 0.25 && hitY < 0.3125) {
                    return "number5";
                }
                if (hitX > 0.0625 * 5 && hitX < 0.0625 * 6 && hitY > 0.25 && hitY < 0.3125) {
                    return "number6";
                }
                if (hitX > 0.0625 * 1 && hitX < 0.0625 * 2 && hitY > 0.125 && hitY < 0.1875) {
                    return "number7";
                }
                if (hitX > 0.0625 * 3 && hitX < 0.0625 * 4 && hitY > 0.125 && hitY < 0.1875) {
                    return "number8";
                }
                if (hitX > 0.0625 * 5 && hitX < 0.0625 * 6 && hitY > 0.125 && hitY < 0.1875) {
                    return "number9";
                }
        }
        return "null";
    }
}
