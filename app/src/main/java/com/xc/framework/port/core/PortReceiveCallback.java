package com.xc.framework.port.core;

/**
 * @author ZhangXuanChen
 * @date 2020/3/8
 * @package com.xc.framework.port.serial
 * @description 接收响应回调
 */
public interface PortReceiveCallback {
    /**
     * @param what          标识
     * @param responseDatas 响应数据
     * @author ZhangXuanChen
     * @date 2021/7/16
     * @description 响应
     */
    void onResponse(int what, byte[] responseDatas);

    /**
     * @param what        标识
     * @param resultDatas 结果数据
     * @author ZhangXuanChen
     * @date 2021/7/16
     * @description 结果
     */
    void onResult(int what, byte[] resultDatas);

    /**
     * @param what      标识
     * @param sendDatas 发送数据
     * @author ZhangXuanChen
     * @date 2021/7/16
     * @description 超时
     */
    void onTimeout(int what, byte[] sendDatas);

}
