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
     * @date 2021/8/20
     * @description dispatchDraw
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!XCStringUtil.isEmpty(text)) {
            int width = (int) (canvas.getWidth() - shadowRightRadius * 2 - padding * 2);
            StaticLayout layout = new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            canvas.save();
            canvas.translate(getWidth() / 2f, getHeight() / 2f - layout.getHeight() / 2f);
            layout.draw(canvas);
            canvas.restore();
        }
    }

}
