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
    private ObjectArrayList<RenderComponent> children = new ObjectArrayList<>();
    private World world;
    private BlockPos blockPos;
    private StoredMatrixTransformations storedMatrixTransformations;
    private float width, height;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private float x = 0, y = 0, z = 0;
    private float coordinateOriginX = 0, coordinateOriginY = 0, coordinateOriginZ = 0;
    private float parentWidth, parentHeight;
    private float parentCoordinateOriginX = 0, parentCoordinateOriginY = 0, parentCoordinateOriginZ;
    private Gravity gravity = Gravity.START;
    private layoutWidth widthType = layoutWidth.WRAP_CONTENT;
    private layoutHeight heightType = layoutHeight.WRAP_CONTENT;
    private String id = "linearlayout";
    private int backgroundColor = 0x00000000;


    public LinearLayout(Boolean isVertical) {
        this.isVertical = isVertical;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void render() {
        setParentCoordinateOrigin();
        calculateLayoutWidth();
        calculateLayoutHeight();
        calculateGravityOffset(); // 根据最新尺寸计算偏移

        BlockState blockState = world.getBlockState(blockPos);
        Direction facing = IBlock.getStatePropertySafe(blockState, FACING);

        StoredMatrixTransformations storedMatrixTransformations3 = storedMatrixTransformations.copy();
        storedMatrixTransformations3.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
        });

        MainRenderer.scheduleRender(new Identifier(Init.MOD_ID, "textures/block/white.png"), false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, offset) -> {
				storedMatrixTransformations3.transform(graphicsHolder, offset);
				IDrawing.drawTexture(graphicsHolder, x, y, width, height,0,0,1,1, Direction.UP, backgroundColor, 15);
				graphicsHolder.pop();
			});

        float offset = 0;
        for (RenderComponent child : children) {
            float[] margin = child.getMargin();
            float remainingWidth = 0;
            float remainingHeight = 0;
            if (isVertical) {
                child.setPosition(x, y + height - child.getHeight() - margin[1] - margin[3] - offset, z);
                offset += y + height - child.getHeight() - margin[1] - margin[3];
                remainingHeight = height - offset - margin[1] - margin[3];

            } else {
                child.setPosition(x + width - child.getWidth() - margin[0] - margin[2] - offset, y, z);
                offset += width - child.getWidth() - margin[0] - margin[2];
                remainingWidth = width - offset - margin[0] - margin[2];
            }
            child.setStoredMatrixTransformations(storedMatrixTransformations);
            child.setParentDimensions(remainingWidth, remainingHeight);
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
    public void setMargin(float left, float top, float right, float bottom) {
        this.marginLeft = left;
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
    }

    @Override
    public void setParentDimensions(float parentWidth,float parentHeight) {//用于设置子控件约束空间
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
    }



    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    public void setParentCoordinateOrigin(){
        this.parentCoordinateOriginX = coordinateOriginX;
        this.parentCoordinateOriginY = coordinateOriginY;
        this.parentCoordinateOriginZ = coordinateOriginZ;
    }

    private void calculateSelfCoordinateOrigin() {
        this.coordinateOriginX = (x + width) / 2;
        this.coordinateOriginY = y;
        this.coordinateOriginZ = z;
    }


    private void calculatePosition(float offsetX, float offsetY, float offsetZ) {
        this.x = parentCoordinateOriginX + offsetX;
        this.y = parentCoordinateOriginY + offsetY;
        this.z = parentCoordinateOriginZ + offsetZ;

        calculateSelfCoordinateOrigin();
    }

    private void calculateGravityOffset() {
        float offsetX, offsetY, offsetZ;
        switch (gravity) {
            case START:
                offsetX = parentWidth - width;
                calculatePosition(offsetX, 0, 0);
                break;

            case START_VERTICAL_CENTER:
                offsetX = parentWidth - width;
                offsetY = (parentHeight - height) / 2;
                calculatePosition(offsetX, offsetY, 0);
                break;

            case END:
                offsetX = -parentWidth / 2;
                calculatePosition(offsetX, 0, 0);
                break;

            case END_VERTICAL_CENTER:
                offsetX = -parentWidth / 2;
                offsetY = (parentHeight - height) / 2;
                calculatePosition(offsetX, offsetY, 0);
                break;

            case TOP:
                offsetY = parentHeight - height;
                calculatePosition(0, offsetY, 0);
                break;

            case TOP_HORIZONTAL_CENTER:
                offsetX = -width / 2;
                offsetY = parentHeight - height;
                calculatePosition(offsetX, offsetY, 0);
                break;

            case BOTTOM:
                offsetY = 0;
                calculatePosition(0, offsetY, 0);
                break;

            case BOTTOM_HORIZONTAL_CENTER:
                offsetX = -width / 2;
                offsetY = 0;
                calculatePosition(offsetX, offsetY, 0);
                break;

            case CENTER:
                offsetX = -width / 2;
                offsetY = (parentHeight - height) / 2;
                calculatePosition(offsetX, offsetY, 0);
        }
    }

    private void calculateLayoutWidth() {
        switch (widthType) {
            case WRAP_CONTENT:
                float tempWidth = 0;
                for (RenderComponent child : children) {
                    float[] margin = child.getMargin();
                    tempWidth += child.getWidth() * 16 + margin[0] + margin[2];
                }
                this.width = tempWidth;
                break;

            case MATCH_PARENT:
                this.width = parentWidth;
                break;

            default:
                this.width = widthType.ordinal();
        }
    }

    private void calculateLayoutHeight() {
        switch (heightType) {
            case WRAP_CONTENT:
                float tempHeight = 0;
                for (RenderComponent child : children) {
                    float[] margin = child.getMargin();
                    tempHeight += child.getHeight() * 16 + margin[1] + margin[3];
                }
                this.height = tempHeight;
                break;

            case MATCH_PARENT:
                this.height = parentHeight;
                break;

            default:
                this.height = heightType.ordinal();
        }
    }


    public void addStoredMatrixTransformations(Consumer<GraphicsHolder> transformation) {
        storedMatrixTransformations.add(transformation);
    }

    public enum Gravity {
        START,
        START_VERTICAL_CENTER,
        END,
        END_VERTICAL_CENTER,
        TOP,
        TOP_HORIZONTAL_CENTER,
        BOTTOM,
        BOTTOM_HORIZONTAL_CENTER,
        CENTER,
    }

    public enum layoutWidth {
        WRAP_CONTENT,
        MATCH_PARENT
    }

    public enum layoutHeight {
        WRAP_CONTENT,
        MATCH_PARENT
    }

    public void setBackgroundColor(int color) {
    this.backgroundColor = color;
}

}



