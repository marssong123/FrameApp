package com.ssjj.mediasdk.videoplayer;

import com.ssjj.mediasdk.log.Log;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by GZ1581 on 2016/6/21
 */

public final class CacheMonitor {
    private static final String TAG = "CacheMonitor";
    private static final long Interval = 3000;

    private WeakReference<IjkMediaPlayer> mIjkMediaPlayer;
    private MonitorThread mMonitorThread;

    private IjkVideoView.CacheMonitorCallback mListener;

    public CacheMonitor() {

    }

    public void setIjkMediaPlayer(IjkMediaPlayer player) {
        if (null != player && null != mIjkMediaPlayer && player.equals(mIjkMediaPlayer.get())) {
            return;
        }

        if (null != mMonitorThread) {
            release();
        }

        if (null != player) {
            mIjkMediaPlayer = new WeakReference<>(player);
            mMonitorThread = new MonitorThread();
            mMonitorThread.start();
        }
    }

    public void setMonitorCallback(IjkVideoView.CacheMonitorCallback l) {
        mListener = l;
    }

    private void release() {
        Log.info(TAG, "call release monitor thread " + mMonitorThread);

        mMonitorThread.exit();
        mMonitorThread.interrupt();
        try {
            mMonitorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.warn(TAG, "wait thread interrupt error");
        }

        mMonitorThread = null;
    }

    private class MonitorThread extends Thread {
        private StringBuffer mLogBuffer;
        private AtomicBoolean mRunning;

        public MonitorThread() {

        }

        @Override
        public void run() {
            while (mRunning.get()) {
                try {
                    IjkMediaPlayer player = mIjkMediaPlayer.get();
                    if (null != player) {
                        mLogBuffer.delete(0, mLogBuffer.length());
                        int decoder = player.getVideoDecoder();
                        float fpsOutput = player.getVideoOutputFramesPerSecond();
                        float fpsDecode = player.getVideoDecodeFramesPerSecond();
                        long videoCachedDuration = player.getVideoCachedDuration();
                        long audioCachedDuration = player.getAudioCachedDuration();
                        long videoCachedBytes = player.getVideoCachedBytes();
                        long audioCachedBytes = player.getAudioCachedBytes();
                        long videoCachedPackets = player.getVideoCachedPackets();
                        long audioCachedPackets = player.getAudioCachedPackets();

                        String decoderName = "unknown";
                        switch (decoder) {
                            case IjkMediaPlayer.FFP_PROPV_DECODER_AVCODEC:
                                decoderName = "ffmpeg";
                                break;
                            case IjkMediaPlayer.FFP_PROPV_DECODER_MEDIACODEC:
                                decoderName = "mediaCodec";
                                break;
                        }

                        mLogBuffer.append(String.format(Locale.US, "width %d, height %d, decoder %s ", player.getVideoWidth(), player.getVideoHeight(), decoderName));
                        mLogBuffer.append(String.format(Locale.US, "fps out %.2f decoder %.2f ", fpsOutput, fpsDecode));
                        mLogBuffer.append(String.format(Locale.US, "video cache %s, %s, %d packets ", formatedDurationMilli(videoCachedDuration), formatedSize(videoCachedBytes), videoCachedPackets));
                        mLogBuffer.append(String.format(Locale.US, "audio cache %s, %s, %d packets", formatedDurationMilli(audioCachedDuration), formatedSize(audioCachedBytes), audioCachedPackets));

                        Log.info(TAG, "buffer info %s", mLogBuffer.toString());

                        if (null != mListener) {
                            mListener.onCacheMonitorStatus(decoderName, fpsOutput, fpsOutput,
                                    videoCachedDuration, audioCachedDuration,
                                    videoCachedBytes, audioCachedBytes,
                                    videoCachedPackets, audioCachedPackets);
                        }
                    }

                    Thread.sleep(Interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (null != mListener) {
                mListener.onCacheMonitorStop();
            }
        }

        @Override
        public synchronized void start() {
            mLogBuffer = new StringBuffer();
            mRunning = new AtomicBoolean(true);

            super.start();

            Log.info(TAG, "call start monitor thread " + this);
        }

        public void exit() {
            mRunning.set(false);
        }
    }

    private static String formatedDurationMilli(long duration) {
        if (duration >= 1000) {
            return String.format(Locale.US, "%.2f sec", ((float) duration) / 1000);
        } else {
            return String.format(Locale.US, "%d msec", duration);
        }
    }

    private static String formatedSize(long bytes) {
        if (bytes >= 100 * 1000) {
            return String.format(Locale.US, "%.2f MB", ((float) bytes) / 1000 / 1000);
        } else if (bytes >= 100) {
            return String.format(Locale.US, "%.1f KB", ((float) bytes) / 1000);
        } else {
            return String.format(Locale.US, "%d B", bytes);
        }
    }
}
