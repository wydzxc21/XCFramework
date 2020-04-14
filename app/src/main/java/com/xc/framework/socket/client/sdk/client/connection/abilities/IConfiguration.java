package com.xc.framework.socket.client.sdk.client.connection.abilities;


import com.xc.framework.socket.client.sdk.client.OkSocketOptions;
import com.xc.framework.socket.client.sdk.client.connection.IConnectionManager;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */

public interface IConfiguration {
    /**
     * 修改参数配置
     *
     * @param okOptions 新的参数配置
     * @return 当前的链接管理器
     */
    IConnectionManager option(OkSocketOptions okOptions);

    /**
     * 获得当前连接管理器的参数配置
     *
     * @return 参数配置
     */
    OkSocketOptions getOption();
}
