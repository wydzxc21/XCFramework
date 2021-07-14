package com.xc.framework.port.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCThread;
import com.xc.framework.util.XCByteUtil;
import com.xc.framework.util.XCThreadUtil;

import java.util.Arrays;

/**
 * Date：2020/3/10
 * Author：ZhangXuanChen
 * Description：串口接收基类
 */
public abstract class PortReceiveThread extends XCThread {
    private final String TAG = "PortReceiveThread";
    private PortParam portParam;//串口参数
    private IPort iPort;//串口工具
    //
    private int frameHeadsType;//帧头类型，1：响应，2：请求
    private byte[] bufferDatas;//缓存数据
    private int bufferPosition;//缓存索引

    /**
     * @param portParam 串口参数
     * @param iPort     串口工具
     * @author ZhangXuanChen
     * @date 2020/3/15
     */
    public PortReceiveThread(PortParam portParam, IPort iPort) {
        this.portParam = portParam;
        this.iPort = iPort;
        bufferDatas = new byte[1024];
        bufferPosition = 0;
    }


    @Override
    protected Object onRun(Handler handler) {
        while (isRun()) {
            try {
                readDatas();
                XCThreadUtil.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onHandler(Message msg) {

    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description readDatas
     */
    byte[] cutDatas;//截取数据

    private void readDatas() {
        byte[] readDatas = iPort.readPort();
        if (readDatas != null && readDatas.length > 0) {
            System.arraycopy(readDatas, 0, bufferDatas, bufferPosition, readDatas.length);
            bufferPosition += readDatas.length;
            cutDatas = Arrays.copyOf(bufferDatas, bufferPosition);
//            Log.i(TAG, "readDatas: " + XCByteUtil.toHexStr(cutDatas, true));
        } else if (cutDatas != null) {
            if (portParam.getReceiveResponseFrameHeads() != null && portParam.getReceiveResponseFrameHeads().length > 0 || portParam.getReceiveRequestFrameHeads() != null && portParam.getReceiveRequestFrameHeads().length > 0) {//设置了帧头
                splitData(cutDatas);
            } else {//未设置帧头
                frameHeadsType = 1;
                result(cutDatas);
            }
            cutDatas = null;
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/13 10:54
     * Description：最前一组接收帧头索引
     */
    private int getFirstFrameHeadPosition(byte[] cutDatas) {
        //获取最前一组接收帧头索引
        int responseFrameHeadPosition = PortFrameUtil.getFirstFrameHeadPosition(portParam.getReceiveResponseFrameHeads(), cutDatas);
        //获取最前一组请求帧头索引
        int requestFrameHeadPosition = PortFrameUtil.getFirstFrameHeadPosition(portParam.getReceiveRequestFrameHeads(), cutDatas);
        //返回最前一组帧头
        if (Math.abs(responseFrameHeadPosition) < Math.abs(requestFrameHeadPosition)) {
            frameHeadsType = 1;//响应
            return responseFrameHeadPosition;
        } else {
            frameHeadsType = 2;//请求
            return requestFrameHeadPosition;
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/13 10:59
     * Description：截取数据
     */
    private void splitData(byte[] cutDatas) {
        if (cutDatas == null || cutDatas.length <= 0) {
            return;
        }
        int firstFrameHeadPosition = getFirstFrameHeadPosition(cutDatas);
        if (firstFrameHeadPosition >= 0) {
            byte[][] splitFirsts = PortFrameUtil.splitDataByFirstFrameHead(firstFrameHeadPosition, 0, cutDatas);
            if (splitFirsts != null && splitFirsts.length > 0) {
                cutDatas = splitFirsts[0];
                int length = portParam.portParamCallback != null ? portParam.portParamCallback.onLength(cutDatas) : 0;//判断指令长度
                if (length > 0) {
                    if (cutDatas.length > length) {
                        byte[][] splitAgains = PortFrameUtil.splitDataByFirstFrameHead(getFirstFrameHeadPosition(cutDatas), length, cutDatas);
                        if (splitAgains != null && splitAgains.length > 0) {
                            result(splitAgains[0]);
                            splitData(splitAgains[1]);
                        }
                    } else if (length == cutDatas.length) {
                        result(cutDatas);
                    }
                }
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
        int length = portParam.portParamCallback != null ? portParam.portParamCallback.onLength(cutDatas) : 0;//判断指令长度
        if (length > 0 && length == cutDatas.length) {
            reset();
            if (frameHeadsType == 1) {//响应
                Log.i(TAG, "指令-接收响应:[" + XCByteUtil.toHexStr(cutDatas, true) + "]");
                PortReceiveCache.getInstance().addResponse(cutDatas);
                onResponse(cutDatas);
            } else if (frameHeadsType == 2) {//请求
                boolean isResult = portParam.portParamCallback != null ? portParam.portParamCallback.onResult(cutDatas) : false;
                if (isResult) {//接收结果
                    Log.i(TAG, "指令-接收结果:[" + XCByteUtil.toHexStr(cutDatas, true) + "]");
                    PortReceiveCache.getInstance().addResult(cutDatas);
                    onRequest(cutDatas, true);
                } else {//接收请求
                    Log.i(TAG, "指令-接收请求:[" + XCByteUtil.toHexStr(cutDatas, true) + "]");
                    onRequest(cutDatas, false);
                }
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
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onResponse
     */
    public abstract void onResponse(byte[] responseDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onRequest
     */
    public abstract void onRequest(byte[] requestDatas, boolean isResult);
}
