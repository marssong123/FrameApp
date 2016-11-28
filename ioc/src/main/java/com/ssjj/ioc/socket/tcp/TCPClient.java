package com.ssjj.ioc.socket.tcp;

import android.os.Process;

import com.ssjj.ioc.event.EventCenterProxy;
import com.ssjj.ioc.event.property.PropertyChanged;
import com.ssjj.ioc.log.L;
import com.ssjj.ioc.property.AdaProperty;
import com.ssjj.ioc.utils.AdaUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by GZ1581 on 2016/5/31
 */

public final class TCPClient {

    private static final String TAG = "TCPClient";

    private static final int TimeWaitPer = 800;
    public static int TIME_OUT_SOCKET = 1000 * 15;

    private String mName;
    private AtomicBoolean mExit;

    private Socket mSocket;
    private DataInputStream mSocketIn;
    private DataOutputStream mSocketOut;

    private Thread mIOThread;
    private BlockingDeque<Runnable> mEventQueue;

    private OnSocketResult mListener;

    private String mIP = "";
    private int mPort = -1;

    public TCPClient(String name, OnSocketResult l) {
        mName = name;
        mExit = new AtomicBoolean(false);
        mEventQueue = new LinkedBlockingDeque<>(4);

        mListener = l;
    }

    public void start() {
        L.info(TAG, "%s start io thread", mName);

        if (null != mIOThread) {
            kill();
        }

        EventCenterProxy.register(this, "onNetWorkAvailable", PropertyChanged.class, false, AdaProperty.NetWorkAvailable.value(), false);

        mExit.set(false);
        mIOThread = new SocketThread(mName);
        mIOThread.start();
    }

    public void kill() {
        L.info(TAG, "%s kill io thread", mName);

        EventCenterProxy.unRegister(this, "onNetWorkAvailable", PropertyChanged.class, AdaProperty.NetWorkAvailable.value());

        mExit.set(true);
        mIOThread.interrupt();
        try {
            mIOThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            L.warn(TAG, "wait thread interrupt error");
        }

        mEventQueue.clear();
        mIOThread = null;
    }

    public void connect(final String ip, final int port) {
        L.info(TAG, "%s call connect ip %s port %d", mName, ip, port);

        queueEvent(new Runnable() {
            @Override
            public void run() {
                disConnectInner();
                mIP = ip;
                mPort = port;
                connectInner(ip, port);
            }
        });
    }

    public void disConnect() {
        L.info(TAG, "%s call disconnect ip %s port %d", mName, mIP, mPort);

        queueEvent(new Runnable() {
            @Override
            public void run() {
                disConnectInner();
                mIP = "";
                mPort = -1;
                mEventQueue.clear();
            }
        });
    }

    public void send(final byte[] data, final int key) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (!isConnected()) {
                    if (mIP.isEmpty() || -1 == mPort) {
                        L.error(TAG, "%s send data, but ip port error", mName);
                        AdaUtils.crashIfDebug("%s %s send data, but ip port error", TAG, mName);
                        return;
                    } else {
                        disConnectInner();
                        connectInner(mIP, mPort);
                    }
                }

                try {
                    if (null != mSocketOut) {
                        mSocketOut.write(data);
                    }
                } catch (Exception e) {
                    L.error(TAG, "%s send data error ip:%s, port:%d, %s", mName, mIP, mPort, e.toString());

                    disConnectInner();
                    if (null != mListener) {

                        mListener.onSendFailed(data, key);
                    }
                }
            }
        });
    }

    public void onNetWorkAvailable(PropertyChanged<Boolean> changed) {
        if (changed.getNewValue()) {
            L.info(TAG, "%s net work available %b", mName, changed.getNewValue());
            if (!mIP.isEmpty() && -1 != mPort) {
                connect(mIP, mPort);
            }
        }
    }

    private void queueEvent(Runnable runnable) {
        mEventQueue.offer(runnable);
    }

    private void connectInner(String ip, int port) {
        L.info(TAG, "%s connect inner ip %s port %d", mName, ip, port);

        try {
            mSocket = new Socket(ip, port);
            mSocket.setTcpNoDelay(true);
            mSocket.setReuseAddress(true);
            mSocketOut = new DataOutputStream(mSocket.getOutputStream());
            mSocketIn = new DataInputStream(mSocket.getInputStream());
        } catch (Exception e) {
            mSocket = null;
            L.error(TAG, "%s ip:%s, port:%d, %s", mName, ip, port, e.toString());

            if (null != mListener) {
                mListener.onSocketConnectError();
            }
        }
    }

    private void disConnectInner() {
        L.info(TAG, "%s disconnect inner ip %s port %d", mName, mIP, mPort);

        try {
            if (null != mSocket && !mSocket.isClosed()) {
                L.info(TAG, "%s close socket", mName);
                mSocket.close();
            }
        } catch (Exception e) {
            L.error(TAG, "%s close socket error %s", mName, e.toString());
        } finally {
            mSocket = null;
        }

        try {
            if (null != mSocketOut) {
                L.info(TAG, "%s close socket out put", mName);
                mSocketOut.close();
            }
        } catch (Exception e) {
            L.error(TAG, "%s close socket out stream error %s", mName, e.toString());
        } finally {
            mSocketOut = null;
        }

        try {
            if (null != mSocketIn) {
                L.info(TAG, "%s close socket input", mName);
                mSocketIn.close();
            }
        } catch (Exception e) {
            L.error(TAG, "%s close socket in stream error %s", mName, e.toString());
        } finally {
            mSocketIn = null;
        }
    }

    private boolean isConnected() {
        return null != mSocket && mSocket.isConnected() && !mSocket.isClosed();
    }

    private class SocketThread extends Thread {
        public SocketThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);

            while (!mExit.get()) {
                try {
                    Thread.sleep(TimeWaitPer, 0);

                    if (mExit.get()) {
                        return;
                    }

                    Runnable event = mEventQueue.poll();
                    if (null != event) {
                        event.run();
                    }

                    if (null == mSocket) {
                        continue;
                    }

                    int backLength = mSocketIn.available();
                    if (0 >= backLength) {
                        continue;
                    }

                    byte[] byteResult = new byte[backLength];
                    mSocketIn.readFully(byteResult);
                    L.debug(TAG, "%s read socket length %d", mName, backLength);

                    if (null != mListener) {
                        mListener.onSocketResult(byteResult);
                    }
                } catch (Exception e) {
                    L.error(TAG, "%s socket thread error %s", mName, e.toString());
                }
            }
        }
    }

    public interface OnSocketResult {
        void onSocketResult(byte[] result);

        void onSocketConnectError();

        void onSendFailed(byte[] data, int key);
    }
}
