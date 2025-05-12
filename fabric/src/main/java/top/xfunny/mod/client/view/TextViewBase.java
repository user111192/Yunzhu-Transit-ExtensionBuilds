package top.xfunny.mod.client.view;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.mod.client.DynamicTextureCache;

import java.awt.*;
import java.util.function.Consumer;

public class TextViewBase {
    private String id;
    private StoredMatrixTransformations storedMatrixTransformations, storedMatrixTransformations1;
    private Font font;
    private int color;
    private int letterSpacing = 0;
    private World world;
    private BlockPos blockPos;
    private float height;
    private float width;
    private boolean needScroll;
    private float textSize;
    private float scrollSpeed;
    private float x, y;
    private float textX;
    private String textureId;
    private float fontSize;
    private float gameTick;
    private String text;
    private DynamicTextureCache.DynamicResource texture;
    private float fixedWidth;
    private float textWidth;
    private float textHeight;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private Direction facing;
    private LiftFloorDisplayView.TextAlign textAlign = LiftFloorDisplayView.TextAlign.RIGHT;//文本默认右对齐
    private Gravity gravity;
    private Consumer<GraphicsHolder> transformation;
}
