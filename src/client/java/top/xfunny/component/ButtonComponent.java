package top.xfunny.component;

import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.client.IDrawing;

public class ButtonComponent implements RenderComponent {
    private final Identifier texture;
    private final int defaultColor;
    private final int hoverColor;
    private boolean isHovered;

    public ButtonComponent(Identifier texture, int defaultColor, int hoverColor) {
        this.texture = texture;
        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
    }

    @Override
    public void render(GraphicsHolder graphicsHolder, int light, int overlay) {
        int color = isHovered ? hoverColor : defaultColor;
        IDrawing.drawTexture(
            graphicsHolder,
            -0.5F, -0.5F, 1F, 1F,
            0, 0, 1, 1,
            Direction.UP,
            color,
            light
        );
    }

    @Override
    public void setPosition(float x, float y, float z) {
        // 预留
    }

    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
    }
}
