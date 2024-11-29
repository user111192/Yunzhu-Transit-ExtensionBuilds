package top.xfunny.component;

import org.mtr.mapping.holder.*;
import org.mtr.mod.Init;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;
import top.xfunny.block.TestLiftButtons;
import top.xfunny.block.base.LiftButtonsBase;
import top.xfunny.layout.LinearLayout;
import top.xfunny.layout.RenderComponent;

import static org.mtr.mapping.mapper.DirectionHelper.FACING;
import static org.mtr.mod.data.IGui.SMALL_OFFSET;


public class ButtonView implements RenderComponent {
    boolean[] buttonStates = {false, false};
    private Identifier texture;
    private int defaultColor;
    private int hoverColor;
    private int pressedColor;
    private BlockPos blockPos;
    private Direction facing;
    private BlockState blockState;
    private World world;
    private StoredMatrixTransformations storedMatrixTransformations;
    private StoredMatrixTransformations storedMatrixTransformations1;
    private LiftButtonsBase.LiftButtonDescriptor buttonDescriptor;
    private int light;
    private float width, height;
    private float x, y, z;
    private float yUp, yDown;
    private float marginLeft, marginTop, marginRight, marginBottom;
    private float neighborMarginRight, neighborMarginBottom;
    private float spacing;
    private String id = "button";
    private float totalWidth, totalHeight;

    @Override
    public String getId() {
        return id;
    }


    public void render() {
        this.blockState = world.getBlockState(blockPos);
        this.facing = IBlock.getStatePropertySafe(blockState, FACING);

        this.storedMatrixTransformations1 = storedMatrixTransformations.copy();

        storedMatrixTransformations1.add(graphicsHolder -> {
            graphicsHolder.rotateYDegrees(-facing.asRotation());
            graphicsHolder.translate(0, 0, 0.4375 - SMALL_OFFSET);
        });
        dynamicRender();
    }

    private void dynamicRender() {
        final HitResult hitResult = MinecraftClient.getInstance().getCrosshairTargetMapped();
        final boolean lookingAtTopHalf;
        final boolean lookingAtBottomHalf;
        positionY(y, spacing);
        if (hitResult == null || !IBlock.getStatePropertySafe(blockState, TestLiftButtons.UNLOCKED)) {
            lookingAtTopHalf = false;
            lookingAtBottomHalf = false;
        } else {
            final Vector3d hitLocation = hitResult.getPos();
            final double hitY = MathHelper.fractionalPart(hitLocation.getYMapped());
            final boolean inBlock = hitY < 0.5 && Init.newBlockPos(hitLocation.getXMapped(), hitLocation.getYMapped(), hitLocation.getZMapped()).equals(blockPos);
            lookingAtTopHalf = inBlock && (!buttonDescriptor.hasDownButton() || hitY > 0.25);
            lookingAtBottomHalf = inBlock && (!buttonDescriptor.hasUpButton() || hitY < 0.25);
        }

        if (buttonDescriptor.hasDownButton()) {
            // 根据按钮的按下状态和鼠标位置选择不同的渲染层
            MainRenderer.scheduleRender(
                    texture,
                    false,
                    buttonStates[0] || lookingAtBottomHalf ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.EXTERIOR,
                    (graphicsHolder, offset) -> {
                        // 应用存储的矩阵变换
                        storedMatrixTransformations1.transform(graphicsHolder, offset);
                        // 绘制按钮纹理，位置和颜色根据按钮状态和鼠标位置决定
                        IDrawing.drawTexture(
                                graphicsHolder,
                                x,
                                yDown,
                                width,
                                height,
                                0,
                                0,
                                1,
                                1,
                                facing,
                                buttonStates[0] ? pressedColor : lookingAtBottomHalf ? hoverColor : defaultColor,
                                light
                        );
                        // 弹出当前图形状态
                        graphicsHolder.pop();
                    }
            );
        }
        if (buttonDescriptor.hasUpButton()) {
            // 根据按钮的按下状态和鼠标位置选择不同的渲染层
            MainRenderer.scheduleRender(
                    texture,
                    false,
                    buttonStates[1] || lookingAtTopHalf ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.EXTERIOR,
                    (graphicsHolder, offset) -> {
                        storedMatrixTransformations1.transform(graphicsHolder, offset);
                        IDrawing.drawTexture(
                                graphicsHolder,
                                x,
                                yUp,
                                width,
                                height,
                                0,
                                1,
                                1,
                                0,
                                facing,
                                buttonStates[1] ? pressedColor : lookingAtTopHalf ? hoverColor : defaultColor,
                                light
                        );
                        // 弹出当前图形状态
                        graphicsHolder.pop();
                    }
            );
        }
    }

    private void positionY(float y, float spacing) {
        yUp = buttonDescriptor.hasDownButton() ? y + height + spacing : y;
        yDown = y;
    }

    //layout专用
    @Override
    public void setStoredMatrixTransformations(StoredMatrixTransformations storedMatrixTransformations) {
        this.storedMatrixTransformations = storedMatrixTransformations;
    }

    @Override
    public float[] getMargin() {
        return new float[]{marginLeft, marginTop, marginRight, marginBottom};
    }

    @Override
    public float[] getNeighborMargin() {
        return new float[]{neighborMarginRight, neighborMarginBottom};
    }

    @Override
    public void setParentIsVertical(Boolean isVertical) {

    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    //todo:bug:双按钮下高度不准确
    @Override
    public float getHeight() {
        if (buttonDescriptor.hasUpButton() && buttonDescriptor.hasDownButton()) {
            return height * 2 + spacing;
        } else {
            return height;
        }
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setParentHeight(float parentHeight) {
        this.totalHeight = parentHeight;
    }

    public void setParentWidth(float parentWidth) {
        this.totalWidth = parentWidth;
    }

    @Override
    public void setMargin(float left, float top, float right, float bottom) {
        this.marginLeft = left;
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void setParentDimensions(float parentWidth, float parentHeight) {

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

    public void setBasicsAttributes(World world, BlockPos blockPos) {
        this.world = world;
        this.blockPos = blockPos;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public void setDownButtonLight() {
        buttonStates[0] = true;
    }

    public void setUpButtonLight() {
        buttonStates[1] = true;
    }

    public void setDescriptor(LiftButtonsBase.LiftButtonDescriptor descriptor) {
        buttonDescriptor = descriptor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setPressedColor(int pressedColor) {
        this.pressedColor = pressedColor;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }
}
