package com.xc.framework.socket.core.iocore;


import com.xc.framework.socket.core.exceptions.WriteException;
import com.xc.framework.socket.core.iocore.interfaces.IIOCoreOptions;
import com.xc.framework.socket.core.iocore.interfaces.IOAction;
import com.xc.framework.socket.core.iocore.interfaces.IPulseSendable;
import com.xc.framework.socket.core.iocore.interfaces.ISendable;
import com.xc.framework.socket.core.iocore.interfaces.IStateSender;
import com.xc.framework.socket.core.iocore.interfaces.IWriter;
import com.xc.framework.socket.core.utils.BytesUtils;
import com.xc.framework.socket.core.utils.SLog;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */

public class WriterImpl implements IWriter<IIOCoreOptions> {

    private volatile IIOCoreOptions mOkOptions;

    private IStateSender mStateSender;

    private OutputStream mOutputStream;

    private LinkedBlockingQueue<ISendable> mQueue = new LinkedBlockingQueue<>();

    @Override
    public void initialize(OutputStream outputStream, IStateSender stateSender) {
        mStateSender = stateSender;
        mOutputStream = outputStream;
    }

    @Override
    public boolean write() throws RuntimeException {
        ISendable sendable = null;
        try {
            sendable = mQueue.take();
        } catch (InterruptedException e) {
            //ignore;
        }

        if (sendable != null) {
            try {
                byte[] sendBytes = sendable.parse();
                int packageSize = mOkOptions.getWritePackageBytes();
                int remainingCount = sendBytes.length;
                ByteBuffer writeBuf = ByteBuffer.allocate(packageSize);
                writeBuf.order(mOkOptions.getWriteByteOrder());
                int index = 0;
                while (remainingCount > 0) {
                    int realWriteLength = Math.min(packageSize, remainingCount);
                    writeBuf.clear();
                    writeBuf.rewind();
                    writeBuf.put(sendBytes, index, realWriteLength);
                    writeBuf.flip();
                    byte[] writeArr = new byte[realWriteLength];
                    writeBuf.get(writeArr);
                    mOutputStream.write(writeArr);
                    mOutputStream.flush();

                    if (SLog.isDebug()) {
                        byte[] forLogBytes = Arrays.copyOfRange(sendBytes, index, index + realWriteLength);
                        SLog.i("write bytes: " + BytesUtils.toHexStringForLog(forLogBytes));
                        SLog.i("bytes write length:" + realWriteLength);
                    }

                    index += realWriteLength;
                    remainingCount -= realWriteLength;
                }
                if (sendable instanceof IPulseSendable) {
                    mStateSender.sendBroadcast(IOAction.ACTION_PULSE_REQUEST, sendable);
                } else {
                    mStateSender.sendBroadcast(IOAction.ACTION_WRITE_COMPLETE, sendable);
                }
            } catch (Exception e) {
                WriteException writeException = new WriteException(e);
                throw writeException;
            }
            return true;
        }
        return false;
    }

    @Override
    public void setOption(IIOCoreOptions option) {
        mOkOptions = option;
    }

    @Override
    public void offer(ISendable sendable) {
        mQueue.offer(sendable);
    }

    @Override
    public void close() {
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }


}
