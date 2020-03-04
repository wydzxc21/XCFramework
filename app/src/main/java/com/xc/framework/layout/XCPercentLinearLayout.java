package com.xc.framework.layout;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Author：ZhangXuanChen
 * Time：2019/12/6 14:18
 * Description：XCPercentLinearLayout
 * Param：xmlns:app="http://schemas.android.com/apk/res-auto"
 * Param：app:layout_widthPercentXC="50%sh"
 * Param：app:layout_heightPercentXC="50%sw"
 * Param：sw:屏幕宽,w：父容器宽
 */
public class XCPercentLinearLayout extends LinearLayout {

    private static final String TAG = "PercentLinearLayout";
    private XCPercentLayoutHelper mXCPercentLayoutHelper;

    public XCPercentLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mXCPercentLayoutHelper = new XCPercentLayoutHelper(this);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int tmpHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int tmpWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);

        //fixed scrollview height problems
        if (heightMode == MeasureSpec.UNSPECIFIED && getParent() != null && (getParent() instanceof ScrollView)) {
            int baseHeight = 0;
            Context context = getContext();
            if (context instanceof Activity) {
                Activity act = (Activity) context;
                int measuredHeight = act.findViewById(android.R.id.content).getMeasuredHeight();
                baseHeight = measuredHeight;
            } else {
                baseHeight = getScreenHeight();
            }
            tmpHeightMeasureSpec = MeasureSpec.makeMeasureSpec(baseHeight, heightMode);
        }


        mXCPercentLayoutHelper.adjustChildren(tmpWidthMeasureSpec, tmpHeightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mXCPercentLayoutHelper.handleMeasuredStateTooSmall()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private int getScreenHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mXCPercentLayoutHelper.restoreOriginalParams();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }


    public static class LayoutParams extends LinearLayout.LayoutParams
            implements XCPercentLayoutHelper.PercentLayoutParams {
        private XCPercentLayoutHelper.PercentLayoutInfo mPercentLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            mPercentLayoutInfo = XCPercentLayoutHelper.getPercentLayoutInfo(c, attrs);
        }

        @Override
        public XCPercentLayoutHelper.PercentLayoutInfo getPercentLayoutInfo() {
            return mPercentLayoutInfo;
        }

        @Override
        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            XCPercentLayoutHelper.fetchWidthAndHeight(this, a, widthAttr, heightAttr);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }


        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

    }

}
