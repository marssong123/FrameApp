package com.ssjj.biz.videoplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ssjj.biz.module.report.Report;
import com.ssjj.biz.module.report.ReportKey;
import com.ssjj.biz.module.statistics.umeng.UmengModule;
import com.ssjj.biz.ui.widget.BizFragment;
import com.ssjj.mediasdk.videoplayer.IjkVideoView;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by GZ1581 on 2016/6/22
 */

public class VideoPlayer extends FrameLayout {
    private IjkVideoView mPlayer;
    private MediaCodecMonitor mMonitor;
    private CacheReport mCacheReport;

    public VideoPlayer(Context context) {
        super(context);
        initVideoPlayer(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoPlayer(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoPlayer(context);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        mCacheReport = new CacheReport();
//
//        mPlayer = new IjkVideoView(inflater.getContext());
//        mPlayer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        mPlayer.setMonitorCallback(new IjkVideoView.CacheMonitorCallback() {
//            @Override
//            public void onCacheMonitorStatus(String decoder, float fpsOutput, float fpsDecode,
//                                             long videoCachedDuration, long audioCachedDuration,
//                                             long videoCachedBytes, long audioCachedBytes,
//                                             long videoCachedPackets, long audioCachedPackets) {
//                mCacheReport.report(decoder, fpsOutput, fpsDecode,
//                        videoCachedDuration, audioCachedDuration,
//                        videoCachedBytes, audioCachedBytes,
//                        videoCachedPackets, audioCachedPackets);
//            }
//
//            @Override
//            public void onCacheMonitorStop() {
//                mCacheReport.stop();
//            }
//        });
//
//        return mPlayer;
//    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mPlayer = new IjkVideoView(inflater.getContext());
//        mPlayer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//        return mPlayer;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mMonitor = new MediaCodecMonitor();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        mPlayer.suspend();
//        mMonitor.stop();
//    }

    public void start() {
        mPlayer.start();
    }

    public void stop() {
        mPlayer.stop();
        mMonitor.stop();
    }
    public void release(boolean clearTargetData){
        mPlayer.release(clearTargetData);
    }
    public void pause(){
        mPlayer.pause();
    }

    public void resume(){
        mPlayer.resume();
    }


    public int getDuration() {
        if (mPlayer != null) return mPlayer.getDuration();
        return 0;
    }

    public int getCurrentPosition() {
        if (mPlayer != null) return mPlayer.getCurrentPosition();
        return 0;
    }

    public void seekTo(int msec) {
        if (mPlayer != null) mPlayer.seekTo(msec);
    }

    public boolean isPlaying() {
        if (mPlayer != null) return mPlayer.isPlaying();
        return false;
    }

    public int getBufferPercentage() {
        if (mPlayer != null) return mPlayer.getBufferPercentage();
        return 0;
    }



    public void setRatio(int ratio) {
        if (mPlayer !=null) mPlayer.setAspectRatio(ratio);
    }


    public int getRatio() {
        if (mPlayer != null) return mPlayer.getAspectRatio();
        return 0;
    }



    public void registerCallabck(IjkVideoView.Callback mCallback) {
        if (mPlayer !=null) mPlayer.registerCallback(mCallback);
    }



    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }




    public void setUri(String uri) {
        mPlayer.setVideoURI(Uri.parse(uri));
    }

    public void enableMediaCodec(String uri, boolean mediaCodec) {
        if (mediaCodec) {
            mMonitor.start();
        } else {
            mMonitor.stop();
        }

        mPlayer.enableMediaCodec(Uri.parse(uri), mediaCodec);
    }

    public boolean isMediaCodecSupport() {
        return mPlayer.isMediaCodecSupport() && mMonitor.isMediaCodecSupport();
    }

    public boolean isMediaCodecEnabled() {
        return mPlayer.isMediaCodecEnabled();
    }

    private void initVideoPlayer(Context context) {
        mCacheReport = new CacheReport();
        mMonitor = new MediaCodecMonitor();

        mPlayer = new IjkVideoView(context);
        mPlayer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mPlayer.setMonitorCallback(new IjkVideoView.CacheMonitorCallback() {
            @Override
            public void onCacheMonitorStatus(String decoder, float fpsOutput, float fpsDecode,
                                             long videoCachedDuration, long audioCachedDuration,
                                             long videoCachedBytes, long audioCachedBytes,
                                             long videoCachedPackets, long audioCachedPackets) {
                mCacheReport.report(decoder, fpsOutput, fpsDecode,
                        videoCachedDuration, audioCachedDuration,
                        videoCachedBytes, audioCachedBytes,
                        videoCachedPackets, audioCachedPackets);
            }

            @Override
            public void onCacheMonitorStop() {
                mCacheReport.stop();
            }
        });

        addView(mPlayer);
    }

    private static class CacheReport {
        private long mVideoPlayTimeStamp;
        private long mVideoStopTimeStamp;
        private long mAudioPlayTimeStamp;
        private long mAudioStopTimeStamp;

        public CacheReport() {
            mVideoPlayTimeStamp = 0L;
            mVideoStopTimeStamp = 0L;
            mAudioPlayTimeStamp = 0L;
            mAudioStopTimeStamp = 0L;
        }

        public void report(String decoder, float fpsOutput, float fpsDecode,
                           long videoCachedDuration, long audioCachedDuration,
                           long videoCachedBytes, long audioCachedBytes,
                           long videoCachedPackets, long audioCachedPackets) {

            if (0L < videoCachedPackets) {
                if (0L == mVideoPlayTimeStamp) {
                    mVideoPlayTimeStamp = SystemClock.elapsedRealtime();
                }

                if (0L != mVideoStopTimeStamp) {
                    UmengModule.reportEvent(ReportKey.VideoStopDuration, msecToSec(SystemClock.elapsedRealtime() - mVideoStopTimeStamp));
                    mVideoStopTimeStamp = 0L;
                }
            } else {
                if (0L != mVideoPlayTimeStamp) {
                    UmengModule.reportEvent(ReportKey.VideoPlayDuration, msecToSec(SystemClock.elapsedRealtime() - mVideoPlayTimeStamp));
                    mVideoPlayTimeStamp = 0L;
                }

                if (0L == mVideoStopTimeStamp) {
                    mVideoStopTimeStamp = SystemClock.elapsedRealtime();
                }
            }

            if (0L < audioCachedPackets) {
                if (0L == mAudioPlayTimeStamp) {
                    mAudioPlayTimeStamp = SystemClock.elapsedRealtime();
                }

                if (0L != mAudioStopTimeStamp) {
                    UmengModule.reportEvent(ReportKey.AudioStopDuration, msecToSec(SystemClock.elapsedRealtime() - mAudioStopTimeStamp));
                    mAudioStopTimeStamp = 0L;
                }
            } else {
                if (0L != mAudioPlayTimeStamp) {
                    UmengModule.reportEvent(ReportKey.AudioPlayDuration, msecToSec(SystemClock.elapsedRealtime() - mAudioPlayTimeStamp));
                    mAudioPlayTimeStamp = 0L;
                }

                if (0L == mAudioStopTimeStamp) {
                    mAudioStopTimeStamp = SystemClock.elapsedRealtime();
                }
            }

            HashMap<String, String> map = new HashMap<>(9);
            map.put("decoder", decoder);
            map.put("fpsOutput", String.format(Locale.US, "%.2f", fpsOutput));
            map.put("fpsDecode", String.format(Locale.US, "%.2f", fpsDecode));

            map.put("videoCachedDuration", msecToSec(videoCachedDuration));
            map.put("audioCachedDuration", msecToSec(audioCachedDuration));

            map.put("videoCachedBytes", byteToMB(videoCachedBytes));
            map.put("audioCachedBytes", byteToKByte(audioCachedBytes));

            map.put("videoCachedPackets", String.valueOf(videoCachedPackets));
            map.put("audioCachedPackets", String.valueOf(audioCachedPackets));

            UmengModule.reportEvent(ReportKey.VideoCacheStatus, map);
        }

        public void stop() {
            mVideoPlayTimeStamp = 0L;
            mVideoStopTimeStamp = 0L;
            mAudioPlayTimeStamp = 0L;
            mAudioStopTimeStamp = 0L;
        }

        private String msecToSec(long msec) {
            if (msec < 1000L) {
                return msec < 500L ? "0.4321" : "0.5678";
            } else {
                return String.valueOf(msec / 1000L);
            }
        }

        private String byteToKByte(long bytes) {
            if (bytes < 1000L) {
                return bytes < 500L ? "0.4321" : "0.5678";
            } else {
                return String.valueOf(bytes / 1000L);
            }
        }

        private String byteToMB(long bytes) {
            if (bytes < 1000000L) {//1MB
                if (bytes < 1000L) {//1KB
                    return bytes < 500L ? "0.4321" : "0.5678";
                } else if (bytes < 5000L) { //5KB
                    return bytes < 2500L ? "0.54321" : "0.45678";
                } else {
                    return "0.5";
                }
            } else {
                return String.valueOf(bytes / 1000000L);
            }
        }
    }
}
