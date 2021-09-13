package com.xc.framework.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.xc.framework.R;
import com.xc.framework.util.XCStringUtil;

/**
 * @author ZhangXuanChen
 * @date 2021/8/20
 * @package com.hollcon.nagene.view
 * @description 按钮-阴影，圆角，边框，点击效果
 */
public class XCButton extends XCLayout {
    Context context;
    //text
    TextPaint textPaint;
    String text;
    int textColor;
    float textSize;
    float padding;

    public XCButton(Context context) {
        super(context);
        initView(context, null);
    }

    public XCButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public XCButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/20
     * @description initView
     */
    private void initView(Context context, AttributeSet attrs) {
        this.context = context;
        initAttrs(context, attrs);
        initPaint();
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/20
     * @description initAttrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        if (context == null || attrs == null) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.XCButton);
        text = ta.getString(R.styleable.XCButton_text);
        textColor = ta.getColor(R.styleable.XCButton_textColor, Color.BLACK);
        textSize = ta.getDimension(R.styleable.XCButton_textSize, 20);
        padding = ta.getDimension(R.styleable.XCButton_padding, 0);
        ta.recycle();
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/20
     * @description initPaint
     */
    private void initPaint() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/9/13 16:14
     * @description setText
     */
    public void setText(int strId) {
        if (context == null) {
            return;
        }
        String str = context.getResources().getString(strId);
        setText(str);
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/9/13 16:14
     * @description setText
     */
    public void setText(String str) {
        text = str;
        postInvalidate();
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/9/13 16:19
     * @description setTextSize
     */
    public void setTextSize(float size) {
        if (textPaint == null) {
            return;
        }
        textSize = size;
        textPaint.setTextSize(textSize);
        postInvalidate();
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/9/13 16:22
     * @description setTextColor
     */
    public void setTextColor(int color) {
        if (textPaint == null) {
            return;
        }
        textColor = color;
        textPaint.setColor(textColor);
        postInvalidate();
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/9/13 16:24
     * @description setTextTypeface
     */
    public void setTextTypeface(Typeface typeface) {
        if (textPaint == null) {
            return;
        }
        textPaint.setTypeface(typeface);
        postInvalidate();
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/8/20
     * @description dispatchDraw
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        text = XCStringUtil.toStr(text);
        int width = (int) (canvas.getWidth() - shadowRightRadius * 2 - padding * 2);
        StaticLayout layout = new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        canvas.save();
        canvas.translate(getWidth() / 2f, getHeight() / 2f - layout.getHeight() / 2f);
        layout.draw(canvas);
        canvas.restore();
    }

}
