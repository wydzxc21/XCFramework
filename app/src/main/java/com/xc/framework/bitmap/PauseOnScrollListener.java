package com.xc.framework.bitmap;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.xc.framework.bitmap.task.TaskHandler;
/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap
 * @description
 */
public class PauseOnScrollListener implements OnScrollListener {

    private TaskHandler taskHandler;

    private final boolean pauseOnScroll;
    private final boolean pauseOnFling;
    private final OnScrollListener externalListener;

    /**
     * Constructor
     *
     * @param taskHandler   {@linkplain BitmapLoader} instance for controlling
     * @param pauseOnScroll Whether {@linkplain BitmapLoader#pause() pause loading} during touch scrolling
     * @param pauseOnFling  Whether {@linkplain BitmapLoader#pause() pause loading} during fling
     */
    public PauseOnScrollListener(TaskHandler taskHandler, boolean pauseOnScroll, boolean pauseOnFling) {
        this(taskHandler, pauseOnScroll, pauseOnFling, null);
    }

    /**
     * Constructor
     *
     * @param taskHandler    {@linkplain BitmapLoader} instance for controlling
     * @param pauseOnScroll  Whether {@linkplain BitmapLoader#pause() pause loading} during touch scrolling
     * @param pauseOnFling   Whether {@linkplain BitmapLoader#pause() pause loading} during fling
     * @param customListener Your custom {@link OnScrollListener} for {@linkplain AbsListView list view} which also will
     *                       be get scroll events
     */
    public PauseOnScrollListener(TaskHandler taskHandler, boolean pauseOnScroll, boolean pauseOnFling, OnScrollListener customListener) {
        this.taskHandler = taskHandler;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        externalListener = customListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                taskHandler.resume();
                break;
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                if (pauseOnScroll) {
                    taskHandler.pause();
                }
                break;
            case OnScrollListener.SCROLL_STATE_FLING:
                if (pauseOnFling) {
                    taskHandler.pause();
                }
                break;
        }
        if (externalListener != null) {
            externalListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (externalListener != null) {
            externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
