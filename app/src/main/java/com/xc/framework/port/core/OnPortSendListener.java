package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 发送监听
 */
public interface OnPortSendListener {

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：发送
     */
    void onSend(int what, byte[] sendDatas, int sendCount);

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/18 14:58
     * Description：异常
     */
    void onError(byte[] sendDatas, String msg);
}
