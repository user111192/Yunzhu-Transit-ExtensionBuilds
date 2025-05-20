package top.xfunny.mod.client.view;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;
import org.mtr.mod.Init;
import org.mtr.mod.InitClient;
import top.xfunny.mod.client.util.ClientGetLiftDetails;

public class NewLiftArrowView extends ImageView{
    private Lift lift;
    private ArrowType arrowType;
    private boolean needScroll;
    private float scrollSpeed;

    @Override
    public void render() {
        float gameTick = InitClient.getGameTick();
        ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
        final LiftDirection liftDirection = liftDetails.left();

        final float[] uv = uv(liftDirection, gameTick);
        setUv(uv[0], uv[1], uv[2], uv[3]);



        switch (arrowType) {
            case UP:
                if (liftDirection.equals(LiftDirection.UP)) {
                    super.render();
                }
                break;
            case DOWN:
                if (liftDirection.equals(LiftDirection.DOWN)) {
                    super.render();
                }
                break;
            case BOTH:
                if (liftDirection.equals(LiftDirection.NONE) && lift.getDoorValue() != 0) {
                    super.render();
                }
                break;
            case AUTO:
                if (!liftDirection.equals(LiftDirection.NONE)) {
                    super.render();
                }
                break;
        }
    }


    public void setBasicsAttributes(World world, BlockPos blockPos, Lift lift, ArrowType arrowType) {
        this.world = world;
        this.blockPos = blockPos;
        this.lift = lift;
        this.arrowType = arrowType;
    }

    public void setDimension(float width, float height){
        float scale = height / width;
        setWidth(width);
        setScale(scale);
    }

    public void setDimension(float width){
        setWidth(width);
        setScale(1);
    }

    public void setColor(int color){
        this.color = color;
    }

    private float[] uv(LiftDirection direction, float gameTick) {
        float[] uv = new float[4];
        float uvOffset = (gameTick * scrollSpeed) % 1;
        uv[0] = 0; // u1
        uv[1] = (!direction.equals(LiftDirection.UP) ? 0.0f : 1.0f) + (needScroll && !direction.equals(LiftDirection.NONE) ? uvOffset : 0.0f); // v1
        uv[2] = 1; // u2
        uv[3] = (!direction.equals(LiftDirection.UP) ? 1.0f : 0.0f) + (needScroll && !direction.equals(LiftDirection.NONE) ? uvOffset : 0.0f);// v2
        return uv;
    }

    public enum ArrowType{
        UP,
        DOWN,
        BOTH,
        AUTO
    }

    public void setAnimationScrolling(boolean needScroll, float scrollSpeed){
        this.needScroll = needScroll;
        this.scrollSpeed = scrollSpeed;
    }



}
