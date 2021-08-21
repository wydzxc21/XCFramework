package com.xc.framework.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.xc.framework.R;

/**
 * @author ZhangXuanChen
 * @date 2020/2/13
 * @package com.hollcon.im2500.view
 * @description 帧布局-阴影，圆角，边框，点击效果
 */
@SuppressLint("NewApi")
public class XCLayout extends FrameLayout {
    final String TAG = "XCLayout";
    //shadow
    Paint shadowPaint;
    int shadowColor;
    int shadowOffset = 1;
    float shadowLeftRadius;
    float shadowTopRadius;
    float shadowRightRadius;
    float shadowBottomRadius;
    //background
    Paint backgroundPaint;
    int backgroundColor;
    //save
    Paint savePaint;
    //stroke
    Paint strokePaint;
    int strokeColor;
    float strokeWidth;
    //shade
    Paint shadePaint;
    //round
    Paint roundPaint;
    float roundTopLeftRadius;
    float roundTopRightRadius;
    float roundBottomLeftRadius;
    float roundBottomRightRadius;
    //
    private boolean isClick;

    public XCLayout(Context context) {
        this(context, null);
        initView(context, null);
    }

    public XCLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initView(context, attrs);
    }

    public XCLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/20
     * @description initView
     */
    private void initView(Context context, AttributeSet attrs) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initAttrs(context, attrs);
        initPaint();
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/13
     * @package com.zxc.reagent
     * @description initAttrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        if (context == null || attrs == null) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.XCLayout);
        //shadow
        shadowColor = ta.getColor(R.styleable.XCLayout_shadowColor, Color.BLACK);
        float shadowRadius = ta.getDimension(R.styleable.XCLayout_shadowRadius, 0);
        shadowLeftRadius = ta.getDimension(R.styleable.XCLayout_shadowLeftRadius, shadowRadius);
        shadowTopRadius = ta.getDimension(R.styleable.XCLayout_shadowTopRadius, shadowRadius);
        shadowRightRadius = ta.getDimension(R.styleable.XCLayout_shadowRightRadius, shadowRadius);
        shadowBottomRadius = ta.getDimension(R.styleable.XCLayout_shadowBottomRadius, shadowRadius);
        //background
        backgroundColor = ta.getColor(R.styleable.XCLayout_backgroundColor, Color.WHITE);
        //stroke
        strokeColor = ta.getColor(R.styleable.XCLayout_strokeColor, Color.WHITE);
        strokeWidth = ta.getDimension(R.styleable.XCLayout_strokeWidth, 0);
        //round
        float roundRadius = ta.getDimension(R.styleable.XCLayout_roundRadius, 0);
        roundTopLeftRadius = ta.getDimension(R.styleable.XCLayout_roundTopLeftRadius, roundRadius);
        roundTopRightRadius = ta.getDimension(R.styleable.XCLayout_roundTopRightRadius, roundRadius);
        roundBottomLeftRadius = ta.getDimension(R.styleable.XCLayout_roundBottomLeftRadius, roundRadius);
        roundBottomRightRadius = ta.getDimension(R.styleable.XCLayout_roundBottomRightRadius, roundRadius);
        ta.recycle();
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/13
     * @package com.zxc.reagent
     * @description initPaint
     */
    private void initPaint() {
        //shadow
        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setShadowLayer(getMaxShadowRadius(), 0, 0, shadowColor);
        //background
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(backgroundColor);
        //save
        savePaint = new Paint();
        savePaint.setAntiAlias(true);
        //stroke
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
        //shade
        shadePaint = new Paint();
        shadePaint.setAntiAlias(true);
        shadePaint.setColor(Color.BLACK);
        shadePaint.setAlpha(50);
        //round
        roundPaint = new Paint();
        roundPaint.setAntiAlias(true);
        roundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/21
     * @description setBackgroundColor
     */
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.backgroundPaint.setColor(backgroundColor);
        postInvalidate();
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/19
     * @description getShadowMaxRadius
     */
    private float getMaxShadowRadius() {
        float shadowRadius = shadowLeftRadius;
        if (shadowRadius < shadowTopRadius) {
            shadowRadius = shadowTopRadius;
        } else if (shadowRadius < shadowRightRadius) {
            shadowRadius = shadowRightRadius;
        } else if (shadowRadius < shadowBottomRadius) {
            shadowRadius = shadowBottomRadius;
        }
        return shadowRadius;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/19
     * @description getMaxRoundRadius
     */
    private float getMaxRoundRadius() {
        float roundRadius = roundTopLeftRadius;
        if (roundRadius < roundTopRightRadius) {
            roundRadius = roundTopRightRadius;
        } else if (roundRadius < roundBottomLeftRadius) {
            roundRadius = roundBottomLeftRadius;
        } else if (roundRadius < roundBottomRightRadius) {
            roundRadius = roundBottomRightRadius;
        }
        return roundRadius;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/19
     * @description dispatchDraw
     */

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawShadow(canvas);
        drawBackground(canvas);
        drawSave(canvas);
        super.dispatchDraw(canvas);
        drawStroke(canvas);
        drawShade(canvas);
        drawRound(canvas);
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/13
     * @package com.zxc.reagent
     * @description drawShadow
     */
    private void drawShadow(Canvas canvas) {
        if (!isClickable() || isClickable() && !isClick) {
            if (shadowLeftRadius > 0 || shadowTopRadius > 0 || shadowRightRadius > 0 || shadowBottomRadius > 0) {
                canvas.drawRoundRect(new RectF(shadowLeftRadius + shadowOffset, shadowTopRadius + shadowOffset,
                                canvas.getWidth() - shadowRightRadius - shadowOffset, canvas.getHeight() - shadowBottomRadius - shadowOffset),
                        getMaxRoundRadius(), getMaxRoundRadius(), shadowPaint);
            }
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/20
     * @description drawBackground
     */
    private void drawBackground(Canvas canvas) {
        canvas.drawRoundRect(new RectF(shadowLeftRadius, shadowTopRadius,
                        canvas.getWidth() - shadowRightRadius, canvas.getHeight() - shadowBottomRadius),
                getMaxRoundRadius(), getMaxRoundRadius(), backgroundPaint);
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/13
     * @package com.zxc.reagent
     * @description drawSave
     */
    private void drawSave(Canvas canvas) {
        canvas.saveLayer(new RectF(shadowLeftRadius, shadowTopRadius,
                        canvas.getWidth() - shadowRightRadius, canvas.getHeight() - shadowBottomRadius),
                savePaint, Canvas.ALL_SAVE_FLAG);
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/20
     * @description drawStroke
     */
    private void drawStroke(Canvas canvas) {
        if (strokeWidth > 0) {
            canvas.drawRoundRect(new RectF(shadowLeftRadius, shadowTopRadius,
                            canvas.getWidth() - shadowRightRadius, canvas.getHeight() - shadowBottomRadius),
                    getMaxRoundRadius(), getMaxRoundRadius(), strokePaint);
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/13
     * @package com.zxc.reagent
     * @description drawShade
     */
    private void drawShade(Canvas canvas) {
        if (isClickable() && isClick) {
            canvas.drawRoundRect(new RectF(shadowLeftRadius, shadowTopRadius,
                            canvas.getWidth() - shadowRightRadius, canvas.getHeight() - shadowBottomRadius),
                    0, 0, shadePaint);
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/13
     * @package com.zxc.reagent
     * @description drawRound
     */
    private void drawRound(Canvas canvas) {
        if (roundTopLeftRadius > 0 || roundTopRightRadius > 0 || roundBottomLeftRadius > 0 || roundBottomRightRadius > 0) {
            drawRoundTopLeft(canvas);
            drawRoundTopRight(canvas);
            drawRoundBottomLeft(canvas);
            drawRoundBottomRight(canvas);
            canvas.restore();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/13
     * @description drawRoundTopLeft
     */
    private void drawRoundTopLeft(Canvas canvas) {
        if (roundTopLeftRadius > 0) {
            Path path = new Path();
            path.moveTo(0, roundTopLeftRadius);
            path.lineTo(0, 0);
            path.lineTo(roundTopLeftRadius, 0);
            path.arcTo(new RectF(shadowLeftRadius, shadowTopRadius,
                            roundTopLeftRadius * 2 + shadowRightRadius, roundTopLeftRadius * 2 + shadowBottomRadius),
                    -90, -90);
            path.close();
            canvas.drawPath(path, roundPaint);
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/13
     * @description drawRoundTopRight
     */
    private void drawRoundTopRight(Canvas canvas) {
        if (roundTopRightRadius > 0) {
            int width = getWidth();
            Path path = new Path();
            path.moveTo(width - roundTopRightRadius, 0);
            path.lineTo(width, 0);
            path.lineTo(width, roundTopRightRadius);
            path.arcTo(new RectF(width - roundTopRightRadius * 2 - shadowLeftRadius, shadowTopRadius,
                    width - shadowRightRadius, roundTopRightRadius * 2 + shadowBottomRadius), 0, -90);
            path.close();
            canvas.drawPath(path, roundPaint);
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/13
     * @description drawRoundBottomLeft
     */
    private void drawRoundBottomLeft(Canvas canvas) {
        if (roundBottomLeftRadius > 0) {
            int height = getHeight();
            Path path = new Path();
            path.moveTo(0, height - roundBottomLeftRadius);
            path.lineTo(0, height);
            path.lineTo(roundBottomLeftRadius, height);
            path.arcTo(new RectF(shadowLeftRadius, height - roundBottomLeftRadius * 2 - shadowTopRadius,
                    roundBottomLeftRadius * 2 + shadowRightRadius, height - shadowBottomRadius), 90, 90);
            path.close();
            canvas.drawPath(path, roundPaint);
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/13
     * @description drawRoundBottomRight
     */
    private void drawRoundBottomRight(Canvas canvas) {
        if (roundBottomRightRadius > 0) {
            int height = getHeight();
            int width = getWidth();
            Path path = new Path();
            path.moveTo(width - roundBottomRightRadius, height);
            path.lineTo(width, height);
            path.lineTo(width, height - roundBottomRightRadius);
            path.arcTo(new RectF(width - roundBottomRightRadius * 2 - shadowLeftRadius, height - roundBottomRightRadius * 2 - shadowTopRadius,
                    width - shadowRightRadius, height - shadowBottomRadius), 0, 90);
            path.close();
            canvas.drawPath(path, roundPaint);
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/18
     * @description onTouchEvent
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isClickable() && isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClick = true;
                    postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isClick = false;
                    postInvalidate();
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

}
