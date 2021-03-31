package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 接收监听
 */
public interface OnPortReceiveListener {

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：响应
     */
    void onResponse(byte[] responseDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/29 11:35
     * Description：请求
     */
    void onRequest(byte[] requestDatas, boolean isInterrupt);

}
