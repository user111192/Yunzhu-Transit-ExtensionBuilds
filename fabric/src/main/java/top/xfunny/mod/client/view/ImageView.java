package top.xfunny.mod.client.view;

import org.mtr.mapping.holder.*;
import org.mtr.mod.InitClient;
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
    protected World world;
    protected BlockPos blockPos;
    private Identifier texture;
    private float scale;
    private int light;
    protected int color = ARGB_WHITE;
    private float u1 = 1, u2 = 0, v1 = 1, v2 = 0;
    private QueuedRenderLayer queuedRenderLayer = QueuedRenderLayer.EXTERIOR_TRANSLUCENT;
    private boolean needBlink;
    private float blinkInterval = 0.5f;

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
        float gameTick = InitClient.getGameTick();
        BlockState blockState = world.getBlockState(blockPos);
        Direction facing = IBlock.getStatePropertySafe(blockState, FACING);

        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET));

        // 闪烁逻辑
        boolean shouldRender = true;
        if (needBlink && blinkInterval > 0) {
            int framesPerCycle = (int)(blinkInterval * 20);
            int currentFrame = (int)(gameTick % framesPerCycle);
            shouldRender = currentFrame < (framesPerCycle / 2); // 半周期亮、半周期灭
        }

        if(shouldRender){
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
                                u1,
                                v1,
                                u2,
                                v2,
                                facing,
                                color,
                                light
                        );
                        graphicsHolder.pop();
                    }
            );
        }

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

    protected void setUv(float u1, float v1, float u2, float v2){
        this.u1 = u1;
        this.u2 = u2;
        this.v1 = v1;
        this.v2 = v2;
    }

    public void setAnimationBliking(boolean needBlink, float blinkInterval){
        this.needBlink = needBlink;
        this.blinkInterval = blinkInterval;
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
