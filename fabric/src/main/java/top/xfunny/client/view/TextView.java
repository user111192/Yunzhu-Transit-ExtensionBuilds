package top.xfunny.client.view;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.client.TextureCache;
import top.xfunny.client.resource.TextureList;

import java.awt.*;
import java.util.function.Consumer;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;
import static org.mtr.mod.data.IGui.ARGB_WHITE;
import static org.mtr.mod.data.IGui.SMALL_OFFSET;

public class TextView implements RenderView {
    private String id;
    private StoredMatrixTransformations storedMatrixTransformations, storedMatrixTransformations1;
    private Font font;
    private int color;
    private int letterSpacing = 0;
    private World world;
    private BlockPos blockPos;
    private float height;
    private float width;
    private boolean needScroll;
    private int textSize;
    private float scrollSpeed;
    private float x, y;
    private float textX;
    private String textureId;
    private int fontSize;
    private float gameTick;
    private String text;
    private TextureCache.DynamicResource texture;
    private float fixedWidth;
    private float textWidth;
    private float textHeight;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private LiftFloorDisplayView.TextAlign textAlign = LiftFloorDisplayView.TextAlign.RIGHT;//文本默认右对齐
    private Gravity gravity;
    private Consumer<GraphicsHolder> transformation;
    private Direction facing;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void render() {
        calculateSize();
        calculateTextPositionX();
        BlockState blockState = world.getBlockState(blockPos);
        this.facing = IBlock.getStatePropertySafe(blockState, FACING);
        storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
        });

        if (transformation != null) {
            storedMatrixTransformations1.add(transformation);
        }

        float offset1;
        if (text.length() > textSize && needScroll) {
            offset1 = (gameTick * scrollSpeed) % 1;
            MainRenderer.scheduleRender(
                    texture.identifier,
                    false,
                    QueuedRenderLayer.LIGHT_TRANSLUCENT,
                    (graphicsHolder, offset) -> {
                        storedMatrixTransformations1.transform(graphicsHolder, offset);
                        IDrawing.drawTexture(graphicsHolder, textX, y, fixedWidth, textHeight, offset1 + fixedWidth / textWidth, 1, offset1, 0, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());//楼层数字尺寸设置
                        graphicsHolder.pop();
                    });
        } else {
            MainRenderer.scheduleRender(
                    texture.identifier,
                    false,
                    QueuedRenderLayer.LIGHT_TRANSLUCENT,
                    (graphicsHolder, offset) -> {
                        storedMatrixTransformations1.transform(graphicsHolder, offset);
                        IDrawing.drawTexture(graphicsHolder, textX, y, textWidth, textHeight, 1, 1, 0, 0, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());//楼层数字尺寸设置
                        graphicsHolder.pop();
                    }
            );
        }

    }

    private void calculateSize() {
        this.gameTick = org.mtr.mod.InitClient.getGameTick();
        this.texture = TextureList.instance.renderFont(textureId, text, color, font, fontSize, letterSpacing);
        int rawTextWidth = texture.width;
        int rawTextHeight = texture.height;
        float scale = (float) rawTextHeight / height;
        this.textWidth = rawTextWidth / scale;//缩放处理后的文本宽度
        this.textHeight = rawTextHeight / scale;//缩放处理后的文本高度
        this.fixedWidth = textWidth / text.length() * textSize;
    }

    @Override
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations) {
        this.storedMatrixTransformations = storedMatrixTransformations;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) { //设置初始宽度
        this.width = width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) { //设置初始高度
        this.height = height;
    }

    public void setTextAlign(LiftFloorDisplayView.TextAlign textAlign) {
        this.textAlign = textAlign;
    }

    public void setLetterSpacing(int letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    private void calculateTextPositionX() {
        switch (textAlign) {
            case LEFT:
                textX = x + width - (text.length() > textSize ? fixedWidth : textWidth);
                break;
            case CENTER:
                textX = x + width / 2 - (text.length() > textSize ? fixedWidth : textWidth) / 2;
                break;
            case RIGHT:
                textX = x;
                break;
        }
    }

    @Override
    public void setParentDimensions(float parentWidth, float parentHeight) {//用于设置子控件约束空间
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


    public void setTextScrolling(Boolean needScroll, int textSize, float scrollSpeed) {
        this.needScroll = needScroll;
        this.textSize = textSize;
        this.scrollSpeed = scrollSpeed;
    }


    public void setBasicsAttributes(World world, BlockPos blockPos, Font font, int fontSize, int color) {
        this.world = world;
        this.blockPos = blockPos;
        this.font = font;
        this.fontSize = fontSize;
        this.color = color;
    }

    public void setTextureId(String textureId) {
        this.textureId = textureId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addStoredMatrixTransformations(Consumer<GraphicsHolder> transformation) {
        this.transformation = transformation;
    }

    public enum TextAlign {
        LEFT,
        CENTER,
        RIGHT
    }
}
