package com.ssjj.biz.module.upgrade;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.net.Uri;

import com.ssjj.ioc.iocvalue.IocValue;
import com.ssjj.ioc.event.EventCenterProxy;
import com.ssjj.ioc.event.annotation.IASlot;
import com.ssjj.ioc.event.property.PropertyChanged;
import com.ssjj.ioc.http.HttpClient;
import com.ssjj.ioc.http.StringHandler;
import com.ssjj.ioc.http.downloader.DownLoader;
import com.ssjj.ioc.log.L;
import com.ssjj.ioc.module.AdaModule;
import com.ssjj.ioc.module.ModuleCenter;
import com.ssjj.ioc.property.AdaProperty;
import com.ssjj.ioc.property.IAProperty;
import com.ssjj.ioc.utils.FileUtils;
import com.ssjj.ioc.utils.NotifyUtils;
import com.ssjj.ioc.utils.ProgressNotify;
import com.ssjj.ioc.utils.Version;
import com.ssjj.ioc.utils.VersionUtils;
import com.ssjj.biz.R;
import com.ssjj.biz.application.BizApplication;

import java.io.File;

/**
 * Created by GZ1581 on 2016/6/6
 */
public final class UpgradeModule extends AdaModule {

    private static final String TAG = "UpgradeModule";
    private static final int MaxRetryTime = 1;

    private boolean mIsUpgraded = false;
    private UpgradePolicy mPolicy;
    private String mPatch;

    private boolean mUpgradeFailed = false;
    private int mUpgradeRetried = 0;
    private boolean mIsDownLoading = false;

    private ProgressNotify mProgressNotify;

    public static UpgradeInfo getUpgradeInfo() {
        UpgradeModule instance = ModuleCenter.getModule(UpgradeModule.class);
        if (null == instance.mPatch || instance.mPatch.isEmpty()) {
            return null;
        }

        UpgradeInfo info = new UpgradeInfo();
        info.mPatch = instance.mPatch;
        info.mApkDate = instance.mPolicy.getApkDate();
        info.mApkSize = instance.mPolicy.getApkSize();
        info.mApkVersion = instance.mPolicy.getRecent();
        info.mStrength = instance.mPolicy.getStrength();

        return info;
    }

    public UpgradeModule() {
        super();
        mPolicy = new UpgradePolicy();
    }

    @IASlot
    public void upgrade(UpgradeInterface.CheckUpgrade checkUpgrade) {
        L.info(TAG, "call upgrade");

        if (mIsUpgraded) {
            L.info(TAG, "is upgraded");

            if (null != mPatch && !mPatch.isEmpty()) {
                L.info(TAG, "got upgrade info promptTime %d promptTime %d");
                EventCenterProxy.send(new UpgradeCallback.UpgradeArrived());
            } else if (!mPolicy.needUpgrade(VersionUtils.getLocalVer(BizApplication.gContext))) {
                L.info(TAG, "got upgrade info is recent");
                EventCenterProxy.send(new UpgradeCallback.RecentVersion());
            }

            return;
        }

        mIsUpgraded = true;

        queryPolicy();
    }

    @IAProperty(value = AdaProperty.NetWorkAvailable, bindInit = false)
    public void onNetWorkAvailable(PropertyChanged<Boolean> available) {
        if (!available.getNewValue()) {
            return;
        }

        if (mUpgradeFailed && mUpgradeRetried < MaxRetryTime && mIsUpgraded) {
            mIsUpgraded = false;
            mUpgradeRetried += MaxRetryTime;

            L.info(TAG, "net work available and call upgrade");

            upgrade(null);
        }
    }

    @IASlot
    public void ignoreUpgrade(UpgradeInterface.IgnoreUpgrade ignoreUpgrade) {
        mPolicy.ignoreThisVersion();
    }

    @IASlot
    public void downLoadAndInstall(UpgradeInterface.InstallUpgrade installUpgrade) {
        if (mIsDownLoading) {
            L.warn(TAG, "down loading apk");
            return;
        }

        mIsDownLoading = true;

        File file = FileUtils.getRootDir(BizApplication.gContext);
        if (null == file) {
            L.error(TAG, "sd card not exit");
            mIsDownLoading = false;
            return;
        }

        File apk = new File(file.getAbsolutePath() + "/upgrade/hahalive.apk");
        if (apk.exists()) {
            String md5 = FileUtils.fileMd5(apk);
            if (null != md5 && 0 == mPolicy.getApkMD5().compareTo(md5)) {
                mIsDownLoading = false;
                L.info(TAG, "apk ready exists");
                installApk(apk);
                return;
            }

            apk.delete();
        }

        apk = FileUtils.createFileOnSD(file.getAbsolutePath() + "/upgrade/", "hahalive.apk");
        if (null == apk) {
            L.error(TAG, "create apk file failed");
            mIsDownLoading = false;
            return;
        }

        if (null == mProgressNotify) {
            mProgressNotify = new ProgressNotify(NotifyUtils.getUpgradeNotifyID(), installUpgrade.mAppLogoResId, installUpgrade.mAppLogoResId
                    , R.string.upgrade_downloading_title, R.string.upgrade_downloading_content);
        }

        DownLoader.down(mPolicy.getApkUrl(), apk, new DownLoader.DownLoaderListener() {
            @Override
            public void onSuccess(File file) {
                mProgressNotify.cancel();

                L.info(TAG, "down apk success");

                String md5 = FileUtils.fileMd5(file);
                if (null != md5 && 0 == mPolicy.getApkMD5().compareTo(md5)) {
                    installApk(file);
                }

                mIsDownLoading = false;
            }

            @Override
            public void onFailed(int code, File file) {
                L.error(TAG, "down apk failed %d", code);

                mProgressNotify.cancel();
                mIsDownLoading = false;
            }

            @Override
            public void onProgress(int current, int total) {
                mProgressNotify.setProgress(current);
            }

            @Override
            public void onStart(int total) {
                mProgressNotify.start(total);
            }
        });
    }

    private void queryPolicy() {
        L.info(TAG, "call query upgrade policy");

        String url = BizApplication.gContext.getString(IocValue.isDebuggable() ?
                R.string.upgrade_policy_url_d : R.string.upgrade_policy_url);

        url=url+"?"+System.currentTimeMillis();
        L.info(TAG,"update url"+url);
        HttpClient.get(url, new StringHandler() {
            @Override
            public void onSuccess(int code, String response) {
                boolean ret = mPolicy.setPolicyInfo(response);
                if (ret) {
                    checkUpgrade();
                } else {
                    L.error(TAG, "set policy failed");
                }
            }

            @Override
            public void onFailed(int code, Throwable throwable) {
                L.error(TAG, "query upgrade policy failed code %d, error %s", code, null == throwable ? "" : throwable.toString());
                mUpgradeFailed = true;
            }
        });
    }

    private void checkUpgrade() {
        L.info(TAG, "call checkUpgrade");

        Version version = VersionUtils.getLocalVer(BizApplication.gContext);
        boolean ret = mPolicy.needUpgrade(version);
        if (!ret) {
            L.info(TAG, "not need upgrade version %s, recent %s", version.toString(), mPolicy.getRecent().toString());

            EventCenterProxy.send(new UpgradeCallback.RecentVersion());

            return;
        }

        queryPatch();
    }

    private void queryPatch() {
        L.info(TAG, "call queryPatch");

        HttpClient.get(mPolicy.getPatchUrl(), new StringHandler() {
            @Override
            public void onSuccess(int code, String response) {
                L.info(TAG, "query patch success");

                mPatch = response;
                if (null != mPatch && !mPatch.isEmpty()) {
                    if (mPolicy.isIgnore()) {
                        L.info(TAG, "ignored upgrade version");

                        EventCenterProxy.send(new UpgradeCallback.RecentVersion());

                        return;
                    }

                    EventCenterProxy.send(new UpgradeCallback.UpgradeArrived());
                } else {
                    L.error(TAG, "query patch success but null or empty!");
                }
            }

            @Override
            public void onFailed(int code, Throwable throwable) {
                L.error(TAG, "get upgrade patch code %d, error %s", code, null == throwable ? "" : throwable.toString());
                mUpgradeFailed = true;
            }
        });
    }

    private void installApk(File file) {
        L.info(TAG, "install apk success");

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent installIntent = PendingIntent.getActivity(BizApplication.gContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            installIntent.send();
        } catch (CanceledException e) {
            if (null != e && null != e.toString()) {
                L.error(TAG, "install error " + e.toString());
            }
        }
    }

    public static class UpgradeInfo {
        private UpgradePolicy.Strength mStrength;
        private String mPatch;
        private Version mApkVersion;
        private String mApkSize;
        private String mApkDate;

        public String getPatch() {
            return mPatch;
        }

        public Version getApkVersion() {
            return mApkVersion;
        }

        public String getApkSize() {
            return mApkSize;
        }

        public String getApkDate() {
            return mApkDate;
        }

        public UpgradePolicy.Strength getStrength() {
            return mStrength;
        }
    }
}
