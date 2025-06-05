package top.xfunny.mod.client.client_data;

import org.mtr.core.data.Lift;
import top.xfunny.mixin.MixinLiftSchema;

public class LiftSpeed {
    private double speed;
    private double rawSpeed;
    private Lift lift;

    public LiftSpeed(Lift lift) {
        this.lift = lift;
        formatSpeed();
    }
    private void formatSpeed() {
        if(lift != null){
            rawSpeed  = ((MixinLiftSchema) lift).getSpeed();
        }
        speed = rawSpeed * 1000;
    }

    public double getSpeed() {
        if(speed == 0){
            return 0;
        }else{
            return speed;
        }
    }
}
