package top.xfunny.layout;

import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.render.StoredMatrixTransformations;

public interface RenderComponent {
    public void render();
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations);
    public float getWidth();
    public float getHeight();
    public float[] getMargin();
    public void setMargin(float left, float top, float right, float bottom);
    public void setPosition(float x, float y, float z);
}
