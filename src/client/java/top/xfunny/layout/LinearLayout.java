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
    private float x, y, z;
    private float coordinateOriginX = 0, coordinateOriginY = 0, coordinateOriginZ = 0;
    private float parentWidth, parentHeight;
    private float parentCoordinateOriginX = 0, parentCoordinateOriginY = 0, parentCoordinateOriginZ;
    private Gravity gravity = Gravity.START;
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
        calculateGravityOffset();

        StoredMatrixTransformations storedMatrixTransformations3 = storedMatrixTransformations.copy();
        storedMatrixTransformations3.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
        });

        MainRenderer.scheduleRender(new Identifier(Init.MOD_ID, "textures/block/white.png"), false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
            storedMatrixTransformations3.transform(graphicsHolder, offset);
            IDrawing.drawTexture(graphicsHolder, x, y, width, height, 0, 0, 1, 1, Direction.UP, backgroundColor, 15);
            graphicsHolder.pop();
        });

        float offset = 0;
        for (RenderComponent child : children) {
            float[] margin = child.getMargin();
            float remainingWidth = width;
            float remainingHeight = height;

            child.setParentCoordinateOrigin(coordinateOriginX, coordinateOriginY, coordinateOriginZ);
            child.calculateLayoutWidth();
            child.calculateLayoutHeight();
            child.setParentIsVertical(isVertical);

            if (isVertical) {
                child.setPosition(x, y + height - child.getHeight() - margin[1] - margin[3] - offset, z);
                //System.out.println("id:"+child.getId()+",y"+y+",height:"+height+",childHeight:"+child.getHeight()+",offset:"+offset);
                offset += child.getHeight() + margin[1] + margin[3];
                remainingHeight -= child.getHeight() + offset + margin[1] + margin[3];
                child.setParentDimensions(width, remainingHeight);
            } else {
                child.setPosition(x + width - child.getWidth() - margin[0] - margin[2] - offset, y, z);
                offset += child.getWidth() + margin[0] + margin[2];
                remainingWidth -= child.getWidth() + offset + margin[0] + margin[2];
                child.setParentDimensions(remainingWidth, height);
            }
            child.setStoredMatrixTransformations(storedMatrixTransformations);
            child.calculateGravityOffset();
            child.render();
        }
    }

    public void addChild(RenderComponent child) {
        children.add(child);
    }

    public void setBasicsAttributes(World world, BlockPos blockPos) {
        storedMatrixTransformations = new StoredMatrixTransformations(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations) {
        this.storedMatrixTransformations = storedMatrixTransformations;
    }

    @Override
    public void setPosition(float x, float y, float z) {//用于设置子layout绝对坐标
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public float[] getMargin() {
        return new float[]{marginLeft, marginTop, marginRight, marginBottom};
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
    }

    @Override
    public void setParentDimensions(float parentWidth, float parentHeight) {//用于设置子控件约束空间
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
    }

    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    @Override
    public void setParentCoordinateOrigin(float coordinateOriginX, float coordinateOriginY, float coordinateOriginZ) {//针对child
        this.parentCoordinateOriginX = coordinateOriginX;
        this.parentCoordinateOriginY = coordinateOriginY;
        this.parentCoordinateOriginZ = coordinateOriginZ;
    }

    public void calculateGravityOffset() {
        float offsetX, offsetY, offsetZ;
        switch (gravity) {
            case START:
                if (parentIsVertical) {
                    offsetX = parentWidth / 2 - width;
                    calculateLayoutPosition(offsetX, 0, 0);
                }
                break;

            case VERTICAL_CENTER:
                if (!parentIsVertical) {
                    offsetY = (parentHeight - height) / 2;
                    calculateLayoutPosition(0, offsetY, 0);
                }
                break;

            case END:
                if (parentIsVertical) {
                    offsetX = -parentWidth / 2;
                    calculateLayoutPosition(offsetX, 0, 0);
                }
                break;

            case TOP:
                if (!parentIsVertical) {
                    offsetY = parentHeight - height;
                    calculateLayoutPosition(0, offsetY, 0);
                }
                break;

            case HORIZONTAL_CENTER:
                if (parentIsVertical) {
                    offsetX = -width / 2;
                    calculateLayoutPosition(offsetX, 0, 0);
                }
                break;

            case BOTTOM:
                if (!parentIsVertical) {
                    offsetY = 0;
                    calculateLayoutPosition(0, offsetY, 0);
                }
                break;
        }
    }

    private void calculateLayoutPosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetX != 0) {
            this.x = parentCoordinateOriginX + offsetX;
        }
        if (offsetY != 0) {
            this.y = parentCoordinateOriginY + offsetY;
        }
        if (offsetZ != 0) {
            this.z = parentCoordinateOriginZ + offsetZ;
        }
        calculateSelfCoordinateOrigin();
    }

    private void calculateSelfCoordinateOrigin() {
        this.coordinateOriginX = x + width / 2;
        this.coordinateOriginY = y;
        this.coordinateOriginZ = z;
    }

    public void calculateLayoutWidth() {
        switch (widthType) {
            case WRAP_CONTENT:
                float tempWidth = 0;
                for (RenderComponent child : children) {
                    child.calculateLayoutWidth();
                    tempWidth += child.getWidth();
                }
                this.width = tempWidth;
                break;

            case MATCH_PARENT:
                for (RenderComponent child : children) {
                    child.calculateLayoutWidth();
                }
                this.width = parentWidth;
                break;

            default:
                this.width = widthType.ordinal();
        }
    }

    public void calculateLayoutHeight() {
        switch (heightType) {
            case WRAP_CONTENT:
                float tempHeight = 0;
                for (RenderComponent child : children) {
                    child.calculateLayoutHeight();
                    tempHeight += child.getHeight();
                }
                this.height = tempHeight;
                break;

            case MATCH_PARENT:
                for (RenderComponent child : children) {
                    child.calculateLayoutHeight();
                }
                this.height = parentHeight;
                break;

            default:
                this.height = heightType.ordinal();
        }
    }


    public void addStoredMatrixTransformations(Consumer<GraphicsHolder> transformation) {
        storedMatrixTransformations.add(transformation);
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public enum Gravity {
        START,
        VERTICAL_CENTER,
        END,
        TOP,
        HORIZONTAL_CENTER,
        BOTTOM,
    }

    public enum layoutWidth {
        WRAP_CONTENT,
        MATCH_PARENT
    }

    public enum layoutHeight {
        WRAP_CONTENT,
        MATCH_PARENT
    }

}



