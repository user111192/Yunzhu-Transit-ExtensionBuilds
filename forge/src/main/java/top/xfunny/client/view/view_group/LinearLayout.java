package top.xfunny.client.view.view_group;


import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.Init;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.client.view.Gravity;
import top.xfunny.client.view.LayoutSize;
import top.xfunny.client.view.RenderView;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;
import static org.mtr.mod.data.IGui.SMALL_OFFSET;


public class LinearLayout implements RenderView {
    private final Boolean isVertical;
    private final ObjectArrayList<RenderView> children = new ObjectArrayList<>();
    private World world;
    private BlockPos blockPos;
    private StoredMatrixTransformations storedMatrixTransformations;
    private float width, height;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private float x, y, z;
    private float coordinateOriginX = 0, coordinateOriginY = 0;
    private Object parentType;
    private float parentWidth, parentHeight;
    private Gravity gravity;
    private LayoutSize widthType = LayoutSize.WRAP_CONTENT;
    private LayoutSize heightType = LayoutSize.WRAP_CONTENT;
    private String id;
    private int backgroundColor = 0x00000000;
    private Consumer<GraphicsHolder> transformation;

    public LinearLayout(Boolean isVertical) {
        this.isVertical = isVertical;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void render() {
        BlockState blockState = world.getBlockState(blockPos);
        Direction facing = IBlock.getStatePropertySafe(blockState, FACING);
        if (transformation != null) {
            storedMatrixTransformations.add(transformation);
        }

        calculateLayoutWidth();
        calculateLayoutHeight();
        calculateSelfCoordinateOrigin();

        StoredMatrixTransformations storedMatrixTransformations3 = storedMatrixTransformations.copy();
        storedMatrixTransformations3.add(graphicsHolder -> {
            graphicsHolder.translate(0, 0, 0.44 - SMALL_OFFSET);
        });

        MainRenderer.scheduleRender(
                new Identifier(Init.MOD_ID, "textures/block/white.png"),
                false,
                QueuedRenderLayer.LIGHT_TRANSLUCENT,
                (graphicsHolder, offset) -> {
                    storedMatrixTransformations3.transform(graphicsHolder, offset);
                    IDrawing.drawTexture(graphicsHolder, x, y, width, height, 0, 0, 1, 1, Direction.UP, backgroundColor, 15);
                    graphicsHolder.pop();
                });

        float offset = 0, remainingWidth = width, remainingHeight = height;


        for (RenderView child : children) {
            float[] margin = child.getMargin();
            Gravity childGravity = child.getGravity();
            child.setParentType(this);
            child.setStoredMatrixTransformations(storedMatrixTransformations);

            if (isVertical) {
                child.setParentDimensions(width, remainingHeight);
                child.calculateLayoutWidth();
                child.calculateLayoutHeight();
                float[] childGravityPositionOffset = calculateChildGravityOffset(child.getWidth(), child.getHeight(), margin, childGravity);
                remainingHeight -= child.getHeight() + margin[1] + margin[3];
                child.setPosition(coordinateOriginX + childGravityPositionOffset[0], coordinateOriginY + height - margin[1] - child.getHeight() - offset);
                offset += child.getHeight() + margin[1] + margin[3];
            } else {
                child.setParentDimensions(remainingWidth, height);
                child.calculateLayoutWidth();
                child.calculateLayoutHeight();
                float[] childGravityPositionOffset = calculateChildGravityOffset(child.getWidth(), child.getHeight(), margin, childGravity);
                remainingWidth -= child.getWidth() + margin[0] + margin[2];
                child.setPosition(coordinateOriginX + width / 2 - margin[0] - child.getWidth() - offset, coordinateOriginY + childGravityPositionOffset[1]);
                offset += child.getWidth() + margin[0] + margin[2];
            }
            child.render();
        }
    }

    public void addChild(RenderView child) {
        children.add(child);
    }

    public void setBasicsAttributes(World world, BlockPos blockPos) {
        this.world = world;
        this.blockPos = blockPos;
        this.storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
    }

    @Override
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations) {
        this.storedMatrixTransformations = storedMatrixTransformations;
    }

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float[] getMargin() {
        return new float[]{marginLeft, marginTop, marginRight, marginBottom};
    }


    @Override
    public Gravity getGravity() {
        return gravity;
    }

    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(LayoutSize widthType) {
        this.widthType = widthType;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(LayoutSize heightType) {
        this.heightType = heightType;
    }

    @Override
    public void setMargin(float left, float top, float right, float bottom) {
        this.marginLeft = left;
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
    }

    @Override
    public void setParentDimensions(float parentWidth, float parentHeight) {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
    }


    @Override
    public Object getParentType() {
        return parentType;
    }

    @Override
    public void setParentType(Object thisObject) {
        this.parentType = thisObject;
    }

    @Override
    public float[] calculateChildGravityOffset(float childWidth, float childHeight, float[] childMargin, Gravity childGravity) {
        float[] offset = new float[2];
        if (childGravity == null) {
            offset = new float[]{width / 2 - childWidth - childMargin[0], height - childHeight - childMargin[1]};//默认位于左上角
        } else {
            switch (childGravity) {
                case START -> {
                    if (isVertical) {
                        offset = new float[]{width / 2 - childWidth - childMargin[0], 0};
                    }
                }
                case CENTER_VERTICAL -> {
                    if (!isVertical) {
                        offset = new float[]{0, (height - childHeight) / 2};
                    }
                }
                case END -> {
                    if (isVertical) {
                        offset = new float[]{-width / 2 + childMargin[3], 0};
                    }
                }
                case TOP -> {
                    if (!isVertical) {
                        offset = new float[]{0, height - childHeight - childMargin[2]};
                    }
                }
                case CENTER_HORIZONTAL -> {
                    if (isVertical) {
                        offset = new float[]{-childWidth / 2, 0};
                    }
                }
                case BOTTOM -> {
                    if (!isVertical) {
                        offset = new float[]{0, childMargin[4]};
                    }
                }
            }

        }
        return offset;
    }


    public void calculateLayoutWidth() {
        switch (widthType) {
            case WRAP_CONTENT -> {
                float tempWidth = 0;
                for (RenderView child : children) {
                    float[] margin = child.getMargin();
                    if (!isVertical) {
                        child.calculateLayoutWidth();
                        tempWidth += child.getWidth() + margin[0] + margin[2];
                    } else {
                        child.calculateLayoutWidth();
                        tempWidth = Math.max(tempWidth, child.getWidth() + margin[0] + margin[2]);
                    }
                }
                width = tempWidth;
            }
            case MATCH_PARENT -> width = parentWidth;
            default -> width = widthType.ordinal();
        }
    }

    public void calculateLayoutHeight() {
        switch (heightType) {
            case WRAP_CONTENT -> {
                float tempHeight = 0;
                for (RenderView child : children) {
                    float[] margin = child.getMargin();
                    if (isVertical) {
                        child.calculateLayoutHeight();
                        tempHeight += child.getHeight() + margin[1] + margin[3];
                    } else {
                        child.calculateLayoutHeight();
                        tempHeight = Math.max(tempHeight, child.getHeight() + margin[1] + margin[3]);
                    }

                }
                height = tempHeight;
            }
            case MATCH_PARENT -> height = parentHeight;
            default -> height = heightType.ordinal();
        }
    }

    private void calculateSelfCoordinateOrigin() {
        this.coordinateOriginX = x + width / 2;
        this.coordinateOriginY = y;
    }

    public void addStoredMatrixTransformations(Consumer<GraphicsHolder> transformation) {
        this.transformation = transformation;//通常情况下用于调整z轴
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public void reverseChildren() {
        // 将 ObjectArrayList 转换为 List
        List<RenderView> list = children;
        // 使用 Collections.reverse() 方法反转列表
        Collections.reverse(list);
    }

}

