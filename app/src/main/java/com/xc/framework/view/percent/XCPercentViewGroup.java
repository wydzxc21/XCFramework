package com.xc.framework.view.percent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Author：ZhangXuanChen
 * Time：2019/12/6 14:22
 * Description：XCPercentViewGroup
 * Param：xmlns:app="http://schemas.android.com/apk/res-auto"
 * Param：app:layout_widthPercentXC="50%h"
 * Param：app:layout_heightPercentXC="50%w"
 * Param：sw:屏幕宽,w：父容器宽
 */
public class XCPercentViewGroup extends ViewGroup {
    private final XCPercentLayoutHelper mHelper = new XCPercentLayoutHelper(this);

    public XCPercentViewGroup(Context context) {
        super(context);
    }

    public XCPercentViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XCPercentViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHelper.adjustChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHelper.handleMeasuredStateTooSmall()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mHelper.restoreOriginalParams();
    }

    public static class LayoutParams extends FrameLayout.LayoutParams
            implements XCPercentLayoutHelper.PercentLayoutParams {
        private XCPercentLayoutHelper.PercentLayoutInfo mPercentLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            mPercentLayoutInfo = XCPercentLayoutHelper.getPercentLayoutInfo(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(FrameLayout.LayoutParams source) {
            super((MarginLayoutParams) source);
            gravity = source.gravity;
        }

        public LayoutParams(LayoutParams source) {
            this((FrameLayout.LayoutParams) source);
            mPercentLayoutInfo = source.mPercentLayoutInfo;
        }

        @Override
        public XCPercentLayoutHelper.PercentLayoutInfo getPercentLayoutInfo() {
            return mPercentLayoutInfo;
        }

        @Override
        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            XCPercentLayoutHelper.fetchWidthAndHeight(this, a, widthAttr, heightAttr);
        }
    }
}
