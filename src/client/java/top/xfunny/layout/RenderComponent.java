package top.xfunny.layout;

import org.mtr.mod.render.StoredMatrixTransformations;

public interface RenderComponent {
    String getId();
    void render();
    void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations);
    float getWidth();
    float getHeight();
    float[] getMargin();
    void setMargin(float left, float top, float right, float bottom);
    void setPosition(float x, float y, float z);
    void setParentDimensions(float parentWidth,float parentHeight);
    void setParentCoordinateOrigin();

}
