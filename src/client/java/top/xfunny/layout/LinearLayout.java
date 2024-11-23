package top.xfunny.layout;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.render.StoredMatrixTransformations;

import java.util.function.Consumer;

public class LinearLayout implements RenderComponent {
    private final ObjectArrayList<RenderComponent> children = new ObjectArrayList<>();
    private final Boolean isVertical;
    private World world;
    private BlockPos blockPos;
    private StoredMatrixTransformations storedMatrixTransformations;
    private float width = 0, height = 0;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private float x, y, z;


    public LinearLayout(Boolean isVertical) {
        this.isVertical = isVertical;
    }

    @Override
    public void render() {
        float offset = 0;
        for (RenderComponent child : children) {
            float[] margin = child.getMargin();

            if (isVertical) {
                child.setPosition(x, y + offset + margin[1], z);
                offset += margin[1] + margin[3] + child.getHeight() * 16;
                width += margin[0] + margin[2] + child.getWidth() * 16;
                height += margin[1] + margin[3] + child.getHeight() * 16;

            } else {
                child.setPosition(x + offset + margin[0], y, z);
                offset += margin[0] + margin[2] + child.getWidth() * 16;
                width += margin[0] + margin[2] + child.getWidth() * 16;
                height += margin[1] + margin[3] + child.getHeight() * 16;
            }
            child.setStoredMatrixTransformations(storedMatrixTransformations);
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
    public void setPosition(float x, float y, float z) {
        this.x = x / 16;
        this.y = y / 16;
        this.z = z / 16;
    }

    @Override
    public float[] getMargin() {
        return new float[]{marginLeft, marginTop, marginRight, marginBottom};
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setMargin(float left, float top, float right, float bottom) {
        this.marginLeft = left / 16;
        this.marginTop = top / 16;
        this.marginRight = right / 16;
        this.marginBottom = bottom / 16;
    }

    public void addStoredMatrixTransformations(Consumer<GraphicsHolder> transformation){
        storedMatrixTransformations.add(transformation);
    }
}
