package com.ssjj.biz.module.share;

import android.content.Intent;
import android.widget.Toast;

import com.ssjj.biz.bean.ShareSignSuccess;
import com.ssjj.biz.bean.ShareSuccess;
import com.ssjj.biz.module.umengsocial.UmengModule;
import com.ssjj.biz.ui.widget.BizActivity;
import com.ssjj.ioc.event.EventCenterProxy;
import com.ssjj.ioc.log.L;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by GZ1581 on 2016/5/25
 */

public final class ShareModule {
    private static final String TAG = "ShareModule";

    public enum SharePlatform {
        WeiXin(SHARE_MEDIA.WEIXIN),
        Circle(SHARE_MEDIA.WEIXIN_CIRCLE),
        QQ(SHARE_MEDIA.QQ),
        QZone(SHARE_MEDIA.QZONE);

        SHARE_MEDIA mPlatform;

        SharePlatform(SHARE_MEDIA platform) {
            mPlatform = platform;
        }

        static SharePlatform fromUMengPlatform(SHARE_MEDIA platform) {
            for (SharePlatform item : SharePlatform.values()) {
                if (platform == item.mPlatform) {
                    return item;
                }
            }

            return null;
        }

        public SHARE_MEDIA value() {
            return mPlatform;
        }
    }

    public static class Params {
        private SharePlatform mPlatform;
        private String mTitle;
        private String mText;
        private String mUrl;
        private String mImageUrl;

        public Params(SharePlatform platform) {
            mPlatform = platform;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public void setText(String text) {
            mText = text;
        }

        public void setUrl(String url) {
            mUrl = url;
        }

        public void setImageUrl(String url) {
            mImageUrl = url;
        }

        public SharePlatform getPlatform() {
            return mPlatform;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getText() {
            return mText;
        }

        public String getUrl() {
            return mUrl;
        }

        public String getImageUrl() {
            return mImageUrl;
        }
    }

    private ShareModule() {

    }

    public static void share(final BizActivity activity, Params params, final int shareType) {
        UmengModule.share(activity, params, new UmengModule.IShareListener() {
            @Override
            public void onNoPackage(SharePlatform platform) {
                L.info(TAG, "未安装 %s", platform.name());
                Toast.makeText(activity.getApplicationContext(),"未安装"+platform.name(),Toast.LENGTH_SHORT).show();
                EventCenterProxy.send(new ShareCallback.ShareResult(ShareCallback.ShareResult.NoPackage,
                        platform, null));

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                SharePlatform p = SharePlatform.fromUMengPlatform(share_media);
                if (null != p) {
                    L.info(TAG, "分享成功 %s", share_media.name());
                    Toast.makeText(activity.getApplicationContext(),"分享成功",Toast.LENGTH_SHORT).show();
                    if(shareType==1){
                        EventBus.getDefault().post(new ShareSignSuccess());
                    }else {

                        EventBus.getDefault().post(new ShareSuccess());
                    }

                } else {
                    L.error(TAG, "share success but unknown type %s", share_media.name());
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                SharePlatform p = SharePlatform.fromUMengPlatform(share_media);
                if (null != p) {
                    String error = null != throwable ? throwable.toString() : " ";
                    L.error(TAG, "分享失败 %s reason %s", share_media.name(), error);
                    Toast.makeText(activity.getApplicationContext(),"分享失败:"+error,Toast.LENGTH_SHORT).show();
                    EventCenterProxy.send(new ShareCallback.ShareResult(ShareCallback.ShareResult.Failed,
                            p, error));
                } else {
                    L.error(TAG, "share failed unknown type %s", share_media.name());
                    Toast.makeText(activity.getApplicationContext(),"share failed unknown type",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                SharePlatform p = SharePlatform.fromUMengPlatform(share_media);
                if (null != p) {
                    L.info(TAG, "取消分享 %s", share_media.name());
                    EventCenterProxy.send(new ShareCallback.ShareResult(ShareCallback.ShareResult.Cancel,
                            p, null));
                } else {
                    L.error(TAG, "share cancel unknown type %s", share_media.name());
                }

            }
        });
    }

    public static void onActivityResult(BizActivity activity, int requestCode, int resultCode, Intent data) {
        UmengModule.onActivityResult(activity, requestCode, resultCode, data);
    }
}
