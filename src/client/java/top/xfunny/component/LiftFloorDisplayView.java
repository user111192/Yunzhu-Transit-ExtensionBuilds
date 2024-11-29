package top.xfunny.component;

import org.mtr.core.data.Lift;
import org.mtr.core.data.LiftDirection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.Init;

import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.InitClient;
import top.xfunny.TextureCache;
import top.xfunny.layout.LinearLayout;
import top.xfunny.layout.RenderComponent;
import top.xfunny.resource.TextureList;
import top.xfunny.util.ClientGetLiftDetails;

import java.awt.*;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;
import static org.mtr.mod.data.IGui.ARGB_WHITE;
import static org.mtr.mod.data.IGui.SMALL_OFFSET;

public class LiftFloorDisplayView implements RenderComponent {
    private final String id = "lift_floor_display";
    private StoredMatrixTransformations storedMatrixTransformations;
    private Font font;
    private int color;
    private World world;
    private BlockPos blockPos;
    private Lift lift;
    private float height;
    private float width;
    private Boolean needScroll;
    private int textSize;
    private float scrollSpeed;
    private float x, y, z;
    private String textureId;
    private int fontSize;
    private boolean noFloorNumber;
    private boolean noFloorDisplay;
    private float gameTick;
    private String text;
    private TextureCache.DynamicResource texture;
    private float fixedWidth;
    private float textWidth;
    private float textHeight;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private BlockState blockState;
    private Direction facing;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void render() {
        calculateSize();
        this.blockState = world.getBlockState(blockPos);
        this.facing = IBlock.getStatePropertySafe(blockState, FACING);
        StoredMatrixTransformations storedMatrixTransformations1 = storedMatrixTransformations.copy();
        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
            graphicsHolder.rotateZDegrees(180);
        });

        if (!noFloorNumber || !noFloorDisplay) {
            float offset1;
            if (text.length() > textSize && needScroll) {
                offset1 = (gameTick * scrollSpeed);
                /*if (offset1 > parentWidth - width1) {
                    offset1 = offset1 - parentWidth;
                }
                float finalOffset = offset1;*/
                float finalOffset1 = offset1;
                MainRenderer.scheduleRender(
                        texture.identifier,
                        false,
                        QueuedRenderLayer.LIGHT_TRANSLUCENT,
                        (graphicsHolder, offset) -> {
                            storedMatrixTransformations1.transform(graphicsHolder, offset);
                            IDrawing.drawTexture(graphicsHolder, x, y, fixedWidth, textHeight, finalOffset1, 0, finalOffset1 + fixedWidth / width, 1, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());//楼层数字尺寸设置
                            graphicsHolder.pop();
                        });
            } else {
                MainRenderer.scheduleRender(
                        texture.identifier,
                        false,
                        QueuedRenderLayer.LIGHT_TRANSLUCENT,
                        (graphicsHolder, offset) -> {
                            storedMatrixTransformations1.transform(graphicsHolder, offset);
                            IDrawing.drawTexture(graphicsHolder, x, y, textWidth, textHeight, 0, 0, 1, 1, Direction.UP, ARGB_WHITE, GraphicsHolder.getDefaultLight());//楼层数字尺寸设置
                            graphicsHolder.pop();
                        });
            }
        }

    }

    private void calculateSize() {
        ObjectObjectImmutablePair<LiftDirection, ObjectObjectImmutablePair<String, String>> liftDetails = ClientGetLiftDetails.getLiftDetails(world, lift, Init.positionToBlockPos(lift.getCurrentFloor().getPosition()));
        String floorNumber = liftDetails.right().left();
        String floorDescription = liftDetails.right().right();
        this.noFloorNumber = floorNumber.isEmpty();
        this.noFloorDisplay = floorDescription.isEmpty();
        this.gameTick = InitClient.getGameTick();
        if (!noFloorNumber || !noFloorDisplay) {
            this.text = String.format("%s%s", floorNumber, noFloorNumber ? " " : "");
            this.texture = TextureList.instance.renderFont(textureId, text, color, font, fontSize);
            int rawTextWidth = texture.width;
            int rawTextHeight = texture.height;
            float scale = Math.max(rawTextWidth, rawTextHeight) / Math.min(width, height);
            this.textWidth = rawTextWidth / scale;//缩放处理后的文本宽度
            this.textHeight = rawTextHeight / scale;//缩放处理后的文本高度
            this.fixedWidth = textWidth / textSize;
            //System.out.println("Text dimensions: " + width + "x" + height);
        }
    }

    @Override
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations) {
        this.storedMatrixTransformations = storedMatrixTransformations;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) { //设置初始宽度
        this.width = width / 16;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) { //设置初始高度
        this.height = height / 16;
    }

  @Override
    public void setParentDimensions(float parentWidth,float parentHeight) {//用于设置子控件约束空间
    }


    @Override
    public void setLayoutGravity(LinearLayout.LayoutGravity layoutGravity) {
    }

    @Override
    public void calculateLayoutWidth() {

    }

    @Override
    public void calculateLayoutHeight() {

    }

    @Override
    public void calculateLayoutGravityOffset() {

    }

    @Override
    public void setParentCoordinateOrigin(float coordinateOriginX, float coordinateOriginY, float coordinateOriginZ) {

    }

    @Override
    public float[] getMargin() {
        return new float[]{marginLeft, marginTop, marginRight, marginBottom};
    }

    @Override
    public float[] getNeighborMargin() {
        return new float[0];
    }

    @Override
    public void setParentIsVertical(Boolean isVertical) {

    }

    @Override
    public void setMargin(float left, float top, float right, float bottom) {
        this.marginLeft = left / 16;
        this.marginTop = top / 16;
        this.marginRight = right / 16;
        this.marginBottom = bottom / 16;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x / 16;
        this.y = y / 16;
        this.z = z / 16;
    }

    public void setTextScrolling(Boolean needScroll, int textSize, float scrollSpeed) {
        this.needScroll = needScroll;
        this.textSize = textSize;
        this.scrollSpeed = scrollSpeed;
    }


    public void setBasicsAttributes(World world, BlockPos blockPos, Lift lift, Font font, int fontSize, int color) {
        this.world = world;
        this.blockPos = blockPos;
        this.lift = lift;
        this.font = font;
        this.fontSize = fontSize;
        this.color = color;
    }

    public void setTextureId(String textureId) {
        this.textureId = textureId;
    }
}
