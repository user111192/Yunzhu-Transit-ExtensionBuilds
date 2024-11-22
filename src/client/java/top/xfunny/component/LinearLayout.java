package top.xfunny.component;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.mapper.GraphicsHolder;

public class LinearLayout implements RenderComponent {
    private final ObjectArrayList<RenderComponent> children = new ObjectArrayList<>();
    private final boolean isVertical; // true: 垂直布局, false: 水平布局
    private float spacing;

    public LinearLayout (Boolean isVertical, float spacing){
        this.isVertical = isVertical;
        this.spacing = spacing;
    }

    public void addChild(RenderComponent child) {
        children.add(child);
    }

    @Override
    public void render(GraphicsHolder graphicsHolder, int light, int overlay) {
        float offset = 0;
        for (RenderComponent child : children) {
            graphicsHolder.push();
            if (isVertical) {
                graphicsHolder.translate(0, offset, 0);
                offset += spacing;
            } else {
                graphicsHolder.translate(offset, 0, 0);
                offset += spacing;
            }
            child.render(graphicsHolder, light, overlay);
            graphicsHolder.pop();
        }
    }

    @Override
    public void setPosition(float x, float y, float z) {
        //预留
    }
}
