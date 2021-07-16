package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 发送监听
 */
public interface OnPortSendListener {
    /**
     * @param what      标识
     * @param sendDatas 发送数据
     * @param sendCount 发送次数
     * @author ZhangXuanChen
     * @date 2021/7/16
     * @description 发送
     */
    void onSend(int what, byte[] sendDatas, int sendCount);

    /**
     * @param sendDatas 发送数据
     * @param msg       消息
     * @author ZhangXuanChen
     * @date 2021/7/16
     * @description 异常
     */
    void onError(byte[] sendDatas, String msg);
}
