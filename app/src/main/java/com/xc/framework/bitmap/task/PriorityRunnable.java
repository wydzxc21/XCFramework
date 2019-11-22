package com.xc.framework.bitmap.task;

/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap.task
 * @description
 */
public class PriorityRunnable extends PriorityObject<Runnable> implements Runnable {

    public PriorityRunnable(Priority priority, Runnable obj) {
        super(priority, obj);
    }

    @Override
    public void run() {
        this.obj.run();
    }
}
