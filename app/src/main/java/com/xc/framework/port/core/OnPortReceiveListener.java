package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 接收监听
 */
public interface OnPortReceiveListener {
    /**
     * @param responseDatas 接收数据
     * @author ZhangXuanChen
     * @date 2021/7/16
     * @description 响应
     */
    void onResponse(byte[] responseDatas);

    /**
     * @param requestDatas 请求数据
     * @param isResult     是否为结果请求
     * @author ZhangXuanChen
     * @date 2021/7/16
     * @description 请求
     */
    void onRequest(byte[] requestDatas, boolean isResult);

}
