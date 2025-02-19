package top.xfunny.view;

import org.mtr.mapping.holder.*;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;
import static org.mtr.mod.data.IGui.ARGB_WHITE;
import static org.mtr.mod.data.IGui.SMALL_OFFSET;

public class ImageView implements RenderView {
    // 基本属性
    private String id;
    private StoredMatrixTransformations storedMatrixTransformations;
    private float width, height;
    private float x, y;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private Gravity gravity;
    private World world;
    private BlockPos blockPos;
    private Identifier texture;
    private float scale;
    private int light;
    private QueuedRenderLayer queuedRenderLayer = QueuedRenderLayer.EXTERIOR;

    // Getters and Setters
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public void setScale(float scale) {
        this.scale = scale;
        calculateDimensions();
    }

    public void setLight(int light) {
        this.light = light;
    }

    public void setLight(int light, QueuedRenderLayer queuedRenderLayer) {
        this.light = light;
        this.queuedRenderLayer = queuedRenderLayer;
    }

    public void setBasicsAttributes(World world, BlockPos blockPos) {
        this.world = world;
        this.blockPos = blockPos;
    }

    // 渲染逻辑
    @Override
    public void render() {
        BlockState blockState = world.getBlockState(blockPos);
        Direction facing = IBlock.getStatePropertySafe(blockState, FACING);

        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET));

        // 调度渲染
        MainRenderer.scheduleRender(
                texture,
                false,
                queuedRenderLayer,
                (graphicsHolder, offset) -> {
                    // 应用矩阵变换
                    storedMatrixTransformations1.transform(graphicsHolder, offset);
                    // 绘制纹理
                    IDrawing.drawTexture(
                            graphicsHolder,
                            x,
                            y,
                            width,
                            height,
                            1,
                            1,
                            0,
                            0,
                            facing,
                            ARGB_WHITE,
                            light
                    );
                    graphicsHolder.pop();
                }
        );
    }

    // 计算尺寸
    public void calculateDimensions() {
        this.height = width * scale;
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
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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

    // 不适用的方法移到最后
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
}
