package top.xfunny.mod.client.view;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;
import static org.mtr.mod.data.IGui.ARGB_WHITE;

public class ImageView implements RenderView {
    public float y;
    // 基本属性
    protected String id;
    protected float width, height;
    protected float x;
    protected World world;
    protected BlockPos blockPos;
    protected int color = ARGB_WHITE;
    private StoredMatrixTransformations storedMatrixTransformations;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private Gravity gravity;
    private Identifier texture;
    private float scale;
    private int light = GraphicsHolder.getDefaultLight();
    private float[] uv;
    private QueuedRenderLayer queuedRenderLayer = QueuedRenderLayer.EXTERIOR;
    private boolean needBlink;
    private float blinkInterval = 0.5f;

    public ImageView() {
        this.uv = new float[]{1, 1, 0, 0};
    }

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

    /**
     * 设置尺寸
     *
     * @param width     宽度
     * @param rawWidth  贴图原始宽度
     * @param rawHeight 贴图原始高度
     */
    public void setDimension(float width, float rawWidth, float rawHeight) {
        float scale = rawHeight / rawWidth;
        setWidth(width);
        this.scale = scale;
        calculateDimensions();
    }

    /**
     * 设置尺寸
     *
     * @param width 宽度
     * @param scale 缩放比：贴图原始高度/贴图原始宽度
     */
    public void setDimension(float width, float scale) {
        setWidth(width);
        this.scale = scale;
        calculateDimensions();
    }

    /**
     * 设置尺寸
     * 宽高比为1:1
     *
     * @param width 宽度
     */
    public void setDimension(float width) {
        setWidth(width);
        this.scale = 1;
        calculateDimensions();
    }

    public void setLight(int light) {
        this.light = light;
    }

    public void setQueuedRenderLayer(QueuedRenderLayer queuedRenderLayer) {
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

        // 闪烁逻辑
        boolean shouldRender = true;
        if (needBlink && blinkInterval > 0) {
            int framesPerCycle = (int) (blinkInterval * 20);
            int currentFrame = (int) (gameTick % framesPerCycle);
            shouldRender = currentFrame < (framesPerCycle / 2); // 半周期亮、半周期灭
        }

        if (shouldRender) {
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
                                uv[0],
                                uv[1],
                                uv[2],
                                uv[3],
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

    protected void setUv(float[] uv) {
        this.uv = uv;
    }

    public void setFlip(boolean flipVertical, boolean flipHorizontal) {
        if (flipVertical) {
            // 垂直翻转
            final float tempV = uv[0];
            uv[0] = uv[2];
            uv[2] = tempV;
        }
        if (flipHorizontal) {
            // 水平翻转
            final float tempU = uv[1];
            uv[1] = uv[3];
            uv[3] = tempU;
        }
    }


    public void setAnimationBliking(boolean needBlink, float blinkInterval) {
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
