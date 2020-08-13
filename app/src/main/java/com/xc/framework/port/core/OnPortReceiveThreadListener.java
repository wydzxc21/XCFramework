package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 接收请求监听
 */
public interface OnPortReceiveThreadListener {
    /**
     * Author：ZhangXuanChen
     * Time：2020/4/29 11:35
     * Description：请求
     */
    void onRequest(byte[] requestDatas);

}
