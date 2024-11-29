package top.xfunny.layout;

import org.mtr.mod.render.StoredMatrixTransformations;

public interface RenderComponent {
    String getId();
    void render();
    void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations);
    float getWidth();
    float getHeight();
    float[] getMargin();
    float[] getNeighborMargin();

    void setParentIsVertical(Boolean isVertical);

    void setMargin(float left, float top, float right, float bottom);

    void setPosition(float x, float y, float z);
    void setParentDimensions(float parentWidth,float parentHeight);
    void setLayoutGravity(LinearLayout.LayoutGravity layoutGravity);

    void calculateLayoutWidth();
    void calculateLayoutHeight();
    void calculateLayoutGravityOffset();
    void setParentCoordinateOrigin(float coordinateOriginX, float coordinateOriginY, float coordinateOriginZ);

}
