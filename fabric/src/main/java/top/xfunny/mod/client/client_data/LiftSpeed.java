package top.xfunny.mod.client.client_data;

import org.mtr.core.data.Lift;
import org.mtr.mapping.holder.ClientWorld;
import top.xfunny.mixin.MixinLiftSchema;
import top.xfunny.mod.Init;
import top.xfunny.mod.client.InitClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LiftSpeed {
    private double rawSpeed;
    private static final float UPDATE_INTERVAL_TICKS = 1; // 20tick/s * 0.5s = 10 ticks
    private final Map<Long, Double> speedCache = new ConcurrentHashMap<>();
    private final Map<Long, Float> tickCache = new ConcurrentHashMap<>();


    public double getSpeed(Lift lift) {
        float gameTick = org.mtr.mod.InitClient.getGameTick();

        long liftId = lift.getId();

        float lastUpdateTick = tickCache.getOrDefault(liftId, 0f);

        if(gameTick < lastUpdateTick){
            lastUpdateTick = gameTick;
            speedCache.clear();
            tickCache.clear();
        }

        if(gameTick - lastUpdateTick >= UPDATE_INTERVAL_TICKS) {
            rawSpeed  = ((MixinLiftSchema) lift).getSpeed()*1000;
            speedCache.put(liftId, rawSpeed);
            tickCache.put(liftId, gameTick);
        }

        return speedCache.getOrDefault(liftId, 0.0);
    }
}
