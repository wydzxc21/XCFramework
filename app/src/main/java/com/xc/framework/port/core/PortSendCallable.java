package com.xc.framework.port.core;

import android.util.Log;

import com.xc.framework.util.XCByteUtil;
import com.xc.framework.util.XCThreadUtil;

import java.util.concurrent.Callable;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：串口发送基类
 */
public abstract class PortSendCallable implements Callable<byte[]> {
    private final String TAG = "PortSendRunnable";
    private IPort iPort;//串口工具
    private PortParam portParam;//串口参数
    private byte[] sendDatas;//发送数据
    private PortReceiveType portReceiveType;//接收类型
    private int what;//区分消息
    private PortFilterCallback portFilterCallback;//过滤回调
    private Object responseLock, resultLock;//响应锁,结果锁
    private PortReceiveCache portReceiveCache;//接收缓存
    //
    private int sendCount;//发送次数

    /**
     * @param iPort              串口工具
     * @param portParam          串口参数
     * @param sendDatas          发送数据
     * @param portReceiveType    接收类型
     * @param what               区分消息
     * @param portFilterCallback 过滤回调
     * @param responseLock       响应锁
     * @param resultLock         结果锁
     * @param portReceiveCache   接收缓存
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public PortSendCallable(IPort iPort, PortParam portParam, byte[] sendDatas, PortReceiveType portReceiveType, int what, PortFilterCallback portFilterCallback, Object responseLock, Object resultLock, PortReceiveCache portReceiveCache) {
        this.iPort = iPort;
        this.portParam = portParam;
        this.sendDatas = sendDatas;
        this.portReceiveType = portReceiveType;
        this.what = what;
        this.portFilterCallback = portFilterCallback;
        this.responseLock = responseLock;
        this.resultLock = resultLock;
        this.portReceiveCache = portReceiveCache;
    }

    @Override
    public byte[] call() throws Exception {
        byte[] receiveDatas = writeDatas();
        if (receiveDatas != null && receiveDatas.length > 0) {
            if (portReceiveType == PortReceiveType.Response) {//响应
                onResponse(what, receiveDatas);
            } else if (portReceiveType == PortReceiveType.Result) {//结果
                onResult(what, receiveDatas);
            }
        } else {//超时
            onTimeout(what, sendDatas);
        }
        return receiveDatas;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description writeDatas
     */
    private byte[] writeDatas() {
        byte[] receiveDatas;
        if (portReceiveType == PortReceiveType.Response) {
            synchronized (responseLock) {
                receiveDatas = waitResponse();
            }
        } else {
            synchronized (resultLock) {
                byte[] responseDatas;
                synchronized (responseLock) {
                    responseDatas = waitResponse();//先等响应
                }
                receiveDatas = waitResult(responseDatas);//再等结果
            }
        }
        return receiveDatas;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/11/8 12:37
     * @description waitResponse
     */
    private byte[] waitResponse() {
        byte[] responseDatas = null;
        while (responseDatas == null && sendCount <= portParam.getResendCount() && !isStopSend()) {
            XCThreadUtil.sleep(10);
            sendCount++;
            iPort.writePort(sendDatas);
            Log.i(TAG, "指令-发送请求:[" + XCByteUtil.toHexStr(sendDatas, true) + "],第" + sendCount + "次");
            onSend(what, sendDatas, sendCount);
            responseDatas = waitReceive(PortReceiveType.Response);
            XCThreadUtil.sleep(1);
        }
        return responseDatas;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/11/8 12:37
     * @description waitResult
     */
    private byte[] waitResult(byte[] responseDatas) {
        if (responseDatas != null && responseDatas.length > 0) {
            byte[] resultDatas = waitReceive(PortReceiveType.Result);
            if (resultDatas != null && resultDatas.length > 0) {
                return resultDatas;
            }
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/9 13:05
     * Description：waitReceive
     */
    private byte[] waitReceive(PortReceiveType receiveType) {
        long currentTime = System.currentTimeMillis();
        byte[] receiveDatas = null;
        while (receiveDatas == null && !isStopSend() && isTimeout(currentTime, receiveType)) {
            if (isPauseReceive()) {
                currentTime = System.currentTimeMillis();
                continue;
            }
            receiveDatas = portReceiveCache.getReceiveDatas(receiveType, sendDatas, portFilterCallback);
            XCThreadUtil.sleep(1);
        }
        return receiveDatas;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 12:45
     * Description：isTimeout
     */
    private boolean isTimeout(long currentTime, PortReceiveType receiveType) {
        long runtime = System.currentTimeMillis() - currentTime;
        long timeout = receiveType == PortReceiveType.Response ? portParam.getSendTimeout() : portParam.getRunTimeout();
        return runtime < timeout;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onResponse
     */
    public abstract void onResponse(int what, byte[] responseDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onResult
     */
    public abstract void onResult(int what, byte[] resultDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 11:18
     * Description：onTimeout
     */
    public abstract void onTimeout(int what, byte[] sendDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 11:18
     * Description：onSend
     */
    public abstract void onSend(int what, byte[] sendDatas, int sendCount);

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 11:18
     * Description：isStopSend
     */
    public abstract boolean isStopSend();

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/10 15:25
     * Description：isPauseReceive
     */
    public abstract boolean isPauseReceive();

}
