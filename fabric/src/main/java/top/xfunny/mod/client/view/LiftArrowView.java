package top.xfunny.mod.client.view;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.Init;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.client.util.ClientGetLiftDetails;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;
import static org.mtr.mod.data.IGui.SMALL_OFFSET;

public class LiftArrowView implements RenderView {
    private String id;
    private StoredMatrixTransformations storedMatrixTransformations;
    private float width, height;
    private float x, y;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private Gravity gravity;
    private World world;
    private BlockPos blockPos;
    private Identifier texture;
    private Lift lift;
    private int upColor, downColor;
    private boolean needScroll;
    private float scrollSpeed;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void render() {
        float gameTick = InitClient.getGameTick();
        BlockState blockState = world.getBlockState(blockPos);
        Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
        });
        ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails =
                ClientGetLiftDetails.getLiftDetails(world, lift, Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
        final LiftDirection liftDirection = liftDetails.left();
        final boolean goingUp = liftDirection == LiftDirection.UP;

        if (liftDirection != LiftDirection.NONE) {
            final float uv = (gameTick * scrollSpeed) % 1;
            final int color = goingUp ? upColor : downColor;
            MainRenderer.scheduleRender(texture, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
                storedMatrixTransformations1.transform(graphicsHolder, offset);
                IDrawing.drawTexture(graphicsHolder, x, y, width, height, 0,
                        (!goingUp ? 0 : 1) + (needScroll ? uv : 0),
                        1, (!goingUp ? 1 : 0) + (needScroll ? uv : 0),
                        Direction.UP, color, GraphicsHolder.getDefaultLight());
                graphicsHolder.pop();
            });
        }
    }

    @Override
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations) {
        this.storedMatrixTransformations = storedMatrixTransformations;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float[] getMargin() {
        return new float[]{marginLeft, marginTop, marginRight, marginBottom};
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public Gravity getGravity() {
        return gravity;
    }

    @Override
    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    @Override
    public void setMargin(float left, float top, float right, float bottom) {
        this.marginLeft = left;
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
    }

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // 不适用的方法
    @Override
    public void setParentDimensions(float parentWidth, float parentHeight) {
        // 不适用
    }

    @Override
    public void calculateLayoutWidth() {
        // 不适用
    }

    @Override
    public void calculateLayoutHeight() {
        // 不适用
    }

    @Override
    public float[] calculateChildGravityOffset(float childWidth, float childHeight, float[] childMargin, Gravity childGravity) {
        return new float[0]; // 不适用
    }

    @Override
    public Object getParentType() {
        return null; // 不适用
    }

    @Override
    public void setParentType(Object thisObject) {
        // 不适用
    }

    // 设置基础属性
    public void setBasicsAttributes(World world, BlockPos blockPos, Lift lift) {
        this.world = world;
        this.blockPos = blockPos;
        this.lift = lift;
    }

    public void setColor(int color) {
        this.upColor = color;
        this.downColor = color;
    }

    public void setColor(int upColor, int downColor) {
        this.upColor = upColor;
        this.downColor = downColor;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public void setArrowScrolling(Boolean needScroll, float scrollSpeed) {
        this.needScroll = needScroll;
        this.scrollSpeed = scrollSpeed;
    }
}
