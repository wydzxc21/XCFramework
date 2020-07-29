package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 接收响应回调
 */
public interface PortReceiveCallback {
    /**
     * @author ZhangXuanChen
     * @date 2020/3/7
     * @description 响应
     */
    void onResponse(int what, byte[] responseDatas);

    /**
     * @author ZhangXuanChen
     * @date 2020/3/7
     * @description 中断
     */
    void onInterrupt(int what, byte[] interruptDatas);

    /**
     * @author ZhangXuanChen
     * @date 2020/3/7
     * @description 超时
     */
    void onTimeout(int what, byte[] sendDatas);

}
