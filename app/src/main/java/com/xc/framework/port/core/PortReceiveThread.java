package com.xc.framework.port.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCThread;
import com.xc.framework.util.XCByteUtil;

import java.util.Arrays;

/**
 * Date：2020/3/10
 * Author：ZhangXuanChen
 * Description：串口接收基类
 */
public abstract class PortReceiveThread extends XCThread {
    private final String TAG = "PortReceiveThread";
    private byte[] responseFrameHeads;//响应帧头
    private byte[] requestFrameHeads;//请求帧头
    private IPort iPort;//串口工具
    //
    private byte[] bufferDatas;//缓存数据
    private int bufferPosition;//缓存索引
    private boolean isResponseFrameHeads;//是否为响应帧头
    private  byte[] responseDatas;//接收响应数据

    /**
     * @param portParam 串口参数
     * @param iPort     串口工具
     * @author ZhangXuanChen
     * @date 2020/3/15
     */
    public PortReceiveThread(PortParam portParam, IPort iPort) {
        this.responseFrameHeads = portParam.getReceiveResponseFrameHeads();
        this.requestFrameHeads = portParam.getReceiveRequestFrameHeads();
        this.iPort = iPort;
        this.bufferDatas = new byte[16 * 1024];
        this.bufferPosition = 0;
    }


    @Override
    protected Object onRun(Handler handler) {
        try {
            while (isRun()) {
                readDatas();
                Thread.sleep(1);
            }
        } catch (Exception e) {
            setRun(false);
        }
        return null;
    }

    @Override
    protected void onHandler(Message msg) {
        switch (msg.what) {
            case 0x123://请求
                onRequest((byte[]) msg.obj);
                break;
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description readDatas
     */
    private void readDatas() {
        byte[] readDatas = iPort.readPort();
        if (readDatas != null && readDatas.length > 0) {
            System.arraycopy(readDatas, 0, bufferDatas, bufferPosition, readDatas.length);
            bufferPosition += readDatas.length;
            byte[] cutDatas = Arrays.copyOf(bufferDatas, bufferPosition);
            isResponseFrameHeads = true;
            if (responseFrameHeads != null && responseFrameHeads.length > 0 || requestFrameHeads != null && requestFrameHeads.length > 0) {//设置了帧头
                if (cutDatas.length >= responseFrameHeads.length || cutDatas.length >= requestFrameHeads.length) {
                    int lastFrameHeadPosition = FrameHeadUtil.getLastFrameHeadPosition(responseFrameHeads, cutDatas);//获取最后一组接收帧头索引
                    if (lastFrameHeadPosition < 0) {
                        isResponseFrameHeads = false;
                        lastFrameHeadPosition = FrameHeadUtil.getLastFrameHeadPosition(requestFrameHeads, cutDatas);//获取最后一组中断帧头索引
                    }
                    if (lastFrameHeadPosition < 0) {
                        reset();
                    } else {
                        cutDatas = FrameHeadUtil.splitDataByLastFrameHead(lastFrameHeadPosition, cutDatas);//根据最后一组帧头索引分割数据
                        result(cutDatas);
                    }
                }
            } else {//未设置帧头
                result(cutDatas);
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/5/18 17:38
     * Description：结果
     */
    private void result(byte[] cutDatas) {
        if (cutDatas == null || cutDatas.length <= 0) {
            return;
        }
        int length = setLength(cutDatas);//判断指令长度
        if (length > 0 && length <= cutDatas.length) {
            reset();
            byte[] datas = Arrays.copyOf(cutDatas, length);//重发粘包根据长度截取
            if (isResponseFrameHeads) {
                Log.i(TAG, "指令-响应:[" + XCByteUtil.byteToHexStr(datas, true) + "]");
                responseDatas = datas;
            } else {
                Log.i(TAG, "指令-请求:[" + XCByteUtil.byteToHexStr(datas, true) + "]");
                sendMessage(0x123, datas);
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 14:51
     * Description：reset
     */
    public void reset() {
        bufferPosition = 0;
        responseDatas = null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/3 9:23
     * Description：getResponseDatas
     */
    public byte[] getResponseDatas() {
        return responseDatas;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 15:07
     * Description：setLength
     */
    public abstract int setLength(byte[] receiveDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onRequest
     */
    public abstract void onRequest(byte[] requestDatas);
}
