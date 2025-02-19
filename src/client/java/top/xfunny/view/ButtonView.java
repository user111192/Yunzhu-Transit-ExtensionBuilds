package top.xfunny.view;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.Init;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;

import java.util.function.Consumer;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;
import static org.mtr.mod.data.IGui.SMALL_OFFSET;

public class ButtonView implements RenderView {
    String hitButton;
    private String id;
    private StoredMatrixTransformations storedMatrixTransformations, storedMatrixTransformations1;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private float x, y;
    private Gravity gravity;
    private float width, height;
    private World world;
    private BlockPos blockPos;
    private int defaultColor, hoverColor;
    private Identifier texture;
    private BlockState blockState;
    private Direction facing;
    private int light;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void render() {
        this.blockState = world.getBlockState(blockPos);
        this.facing = IBlock.getStatePropertySafe(blockState, FACING);
        this.storedMatrixTransformations1 = storedMatrixTransformations.copy();
        final HitResult hitResult = MinecraftClient.getInstance().getCrosshairTargetMapped();
        boolean inBlock;
        if (hitResult != null) {
            final Vector3d hitLocation = hitResult.getPos();
            inBlock = Init.newBlockPos(hitLocation.getXMapped(), hitLocation.getYMapped(), hitLocation.getZMapped()).equals(blockPos);
        } else {
            inBlock = false;
        }

        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
        });

        MainRenderer.scheduleRender(
                texture, false, id.equals(hitButton) && inBlock ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.EXTERIOR,
                (graphicsHolder, offset) -> {
                    storedMatrixTransformations1.transform(graphicsHolder, offset);
                    IDrawing.drawTexture(
                            graphicsHolder,
                            x, y, width, height,
                            1, 1, 0, 0,
                            facing,
                            id.equals(hitButton) && inBlock ? hoverColor : defaultColor,
                            light
                    );
                    graphicsHolder.pop();
                }
        );
    }

    // 布局方法
    @Override
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations) {
        this.storedMatrixTransformations = storedMatrixTransformations;
    }

    @Override
    public float[] getMargin() {
        return new float[]{marginLeft, marginTop, marginRight, marginBottom};
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
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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

    public void setLight(int light) {
        this.light = light;
    }

    // 不适用的布局方法
    @Override
    public void setParentDimensions(float parentWidth, float parentHeight) {
    }

    @Override
    public void calculateLayoutWidth() {
    }

    @Override
    public void calculateLayoutHeight() {
    }

    @Override
    public float[] calculateChildGravityOffset(float childWidth, float childHeight, float[] childMargin, Gravity childGravity) {
        return new float[0];
    }

    @Override
    public Object getParentType() {
        return null;
    }

    @Override
    public void setParentType(Object thisObject) {
    }

    // 设置基本属性的方法
    public void setBasicsAttributes(World world, BlockPos blockPos, String hitButton) {
        this.world = world;
        this.blockPos = blockPos;
        this.hitButton = hitButton;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public void addStoredMatrixTransformations(Consumer<GraphicsHolder> transformation) {
        storedMatrixTransformations1.add(transformation);
    }


}
