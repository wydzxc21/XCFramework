package com.xc.framework.bitmap.task;

/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap.task
 * @description
 */
public interface TaskHandler {

    boolean supportPause();

    boolean supportResume();

    boolean supportCancel();

    void pause();

    void resume();

    void cancel();

    boolean isPaused();

    boolean isCancelled();
}
