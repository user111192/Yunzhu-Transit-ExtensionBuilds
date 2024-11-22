package top.xfunny.component;

import org.mtr.mapping.mapper.GraphicsHolder;

public interface RenderComponent {
    void render(GraphicsHolder graphicsHolder, int light, int overlay);
    void setPosition(float x, float y, float z);
}
