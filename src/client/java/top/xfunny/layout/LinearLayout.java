package top.xfunny.layout;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

public class LinearLayout implements RenderComponent {
    private final Boolean isVertical;
    private Boolean parentIsVertical = true;
    private ObjectArrayList<RenderComponent> children = new ObjectArrayList<>();
    private World world;
    private BlockPos blockPos;
    private StoredMatrixTransformations storedMatrixTransformations;
    private float width, height;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private float neighborMarginRight, neighborMarginBottom;
    private float x, y, z;
    private float coordinateOriginX = 0, coordinateOriginY = 0, coordinateOriginZ = 0;
    private float parentWidth, parentHeight;
    private float parentCoordinateOriginX, parentCoordinateOriginY, parentCoordinateOriginZ;
    private LayoutGravity layoutGravity = LayoutGravity.START;
    private layoutWidth widthType = layoutWidth.WRAP_CONTENT;
    private layoutHeight heightType = layoutHeight.WRAP_CONTENT;
    private String id;
    private int backgroundColor = 0x00000000;

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

        calculateLayoutWidth();
        calculateLayoutHeight();
        calculateLayoutGravityOffset();

        StoredMatrixTransformations storedMatrixTransformations3 = storedMatrixTransformations.copy();
        storedMatrixTransformations3.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
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

        float offset = 0, index = 0, remainingWidth = width, remainingHeight = height;
        for (RenderComponent child : children) {
            float[] margin = child.getMargin();
            float[] neighborMargin = child.getNeighborMargin();

            child.setParentCoordinateOrigin(coordinateOriginX, coordinateOriginY, coordinateOriginZ);
            child.setParentIsVertical(isVertical);
            child.setStoredMatrixTransformations(storedMatrixTransformations);

            if (isVertical) {
                offset += (index == 0) ? margin[1] : 0;
                child.setParentDimensions(width, remainingHeight);
                child.calculateLayoutWidth();
                child.calculateLayoutHeight();
                remainingHeight -= child.getHeight() + margin[1] + margin[3];
                child.setPosition(x, y + height - child.getHeight() - offset, z);
                offset += child.getHeight() + neighborMargin[1];
            } else {
                offset += (index == 0) ? margin[0] : 0;
                child.setParentDimensions(remainingWidth, height);
                child.calculateLayoutWidth();
                child.calculateLayoutHeight();
                remainingWidth -= child.getWidth() + margin[0] + margin[2];
                child.setPosition(x + width - child.getWidth() - offset, y, z);
                offset += child.getWidth() + neighborMargin[0];
            }
            child.render();
            index++;
        }
    }

    public void addChild(RenderComponent child) {
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
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public float[] getMargin() {
        return new float[]{marginLeft, marginTop, marginRight, marginBottom};
    }

    @Override
    public float[] getNeighborMargin() {
        return new float[]{neighborMarginRight, neighborMarginBottom};
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(layoutWidth widthType) {
        this.widthType = widthType;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(layoutHeight heightType) {
        this.heightType = heightType;
    }

    @Override
    public void setParentIsVertical(Boolean isVertical) {
        this.parentIsVertical = isVertical;
    }

    @Override
    public void setMargin(float left, float top, float right, float bottom) {
        this.marginLeft = left;
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
        this.neighborMarginRight = right;
        this.neighborMarginBottom = bottom;
    }

    @Override
    public void setParentDimensions(float parentWidth, float parentHeight) {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
    }

    public void setLayoutGravity(LayoutGravity layoutGravity) {
        this.layoutGravity = layoutGravity;
    }

    @Override
    public void setParentCoordinateOrigin(float coordinateOriginX, float coordinateOriginY, float coordinateOriginZ) {
        this.parentCoordinateOriginX = coordinateOriginX;
        this.parentCoordinateOriginY = coordinateOriginY;
        this.parentCoordinateOriginZ = coordinateOriginZ;
    }

    public void calculateLayoutGravityOffset() {
        switch (layoutGravity) {
            case START -> {
                if (parentIsVertical) {
                    calculateLayoutPosition(parentWidth / 2 - width - marginLeft, 0, 0);
                }
            }
            case VERTICAL_CENTER -> {
                if (!parentIsVertical) {
                    calculateLayoutPosition(-parentWidth / 2, (parentHeight - height) / 2, 0);
                }
            }
            case END -> {
                if (parentIsVertical) {
                    calculateLayoutPosition(-parentWidth / 2 + marginRight, 0, 0);
                }
            }
            case TOP -> {
                if (!parentIsVertical) {
                    calculateLayoutPosition(-parentWidth / 2, parentHeight - height - marginTop, 0);
                }
            }
            case HORIZONTAL_CENTER -> {
                if (parentIsVertical) {
                    calculateLayoutPosition(-width / 2, 0, 0);
                }
            }
            case BOTTOM -> {
                if (!parentIsVertical) {
                    calculateLayoutPosition(-parentWidth / 2, marginBottom, 0);
                }
            }
        }
    }

    private void calculateLayoutPosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetX != 0) x = parentCoordinateOriginX + offsetX;
        if (offsetY != 0) y = parentCoordinateOriginY + offsetY;
        if (offsetZ != 0) z = parentCoordinateOriginZ + offsetZ;
        calculateSelfCoordinateOrigin();
    }

    private void calculateSelfCoordinateOrigin() {
        coordinateOriginX = x + width / 2;
        coordinateOriginY = y;
        coordinateOriginZ = z;
    }

    public void calculateLayoutWidth() {
        switch (widthType) {
            case WRAP_CONTENT -> {
                float tempWidth = 0;
                for (RenderComponent child : children) {
                    child.calculateLayoutWidth();
                    tempWidth += child.getWidth();
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
                for (RenderComponent child : children) {
                    child.calculateLayoutHeight();
                    tempHeight += child.getHeight();
                }
                height = tempHeight;
            }
            case MATCH_PARENT -> height = parentHeight;
            default -> height = heightType.ordinal();
        }
    }

    public void addStoredMatrixTransformations(Consumer<GraphicsHolder> transformation) {
        storedMatrixTransformations.add(transformation);
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public enum LayoutGravity {
        START, VERTICAL_CENTER, END,
            TOP, HORIZONTAL_CENTER, BOTTOM
    }

    public enum layoutWidth {
        WRAP_CONTENT, MATCH_PARENT
    }

    public enum layoutHeight {
        WRAP_CONTENT, MATCH_PARENT
    }
}

