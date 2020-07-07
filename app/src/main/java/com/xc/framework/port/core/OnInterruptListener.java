package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 串口中断监听
 */
public interface OnInterruptListener {
    /**
     * Author：ZhangXuanChen
     * Time：2020/4/29 11:35
     * Description：中断
     */
    void onInterrupt(byte[] interruptDatas);

}
