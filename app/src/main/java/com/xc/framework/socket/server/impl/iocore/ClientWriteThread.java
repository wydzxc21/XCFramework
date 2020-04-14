package com.xc.framework.socket.server.impl.iocore;


import com.xc.framework.socket.common.basic.AbsLoopThread;
import com.xc.framework.socket.core.iocore.interfaces.IStateSender;
import com.xc.framework.socket.core.iocore.interfaces.IWriter;
import com.xc.framework.socket.core.utils.SLog;
import com.xc.framework.socket.server.action.IAction;
import com.xc.framework.socket.server.exceptions.InitiativeDisconnectException;

import java.io.IOException;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */

public class ClientWriteThread extends AbsLoopThread {
    private IStateSender mClientStateSender;

    private IWriter mWriter;

    public ClientWriteThread(IWriter writer, IStateSender clientStateSender) {
        super("server_client_write_thread");
        this.mClientStateSender = clientStateSender;
        this.mWriter = writer;
    }

    @Override
    protected void beforeLoop() {
        mClientStateSender.sendBroadcast(IAction.Client.ACTION_WRITE_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        mWriter.write();
    }

    @Override
    public synchronized void shutdown(Exception e) {
        mWriter.close();
        super.shutdown(e);
    }

    @Override
    protected void loopFinish(Exception e) {
        e = e instanceof InitiativeDisconnectException ? null : e;
        if (e != null) {
            SLog.e("duplex write error,thread is dead with exception:" + e.getMessage());
        }
        mClientStateSender.sendBroadcast(IAction.Client.ACTION_WRITE_THREAD_SHUTDOWN, e);
    }
}
