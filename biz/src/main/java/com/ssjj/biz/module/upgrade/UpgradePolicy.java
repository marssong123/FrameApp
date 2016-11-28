package com.ssjj.biz.module.upgrade;

import android.util.Pair;

import com.ssjj.ioc.log.L;
import com.ssjj.ioc.utils.Version;
import com.ssjj.ioc.utils.VersionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GZ1581 on 2016/6/6
 */
public final class UpgradePolicy {
    private static final String TAG = "UpgradePolicy";

    private PolicyInfo mPolicyInfo;

    public enum Strength {
        Force, //force upgrade
        Urgent, //popup dialog
        Normal, //notification
        Weak //red point
    }

    UpgradePolicy() {
        mPolicyInfo = new PolicyInfo();
    }

    public boolean setPolicyInfo(String info) {
        mPolicyInfo.reset();

        try {
            JSONObject json = new JSONObject(info);
            mPolicyInfo.setStrength(json.getString("strength"));
            mPolicyInfo.setPromptTime(json.getInt("promptTime"));
            mPolicyInfo.setRecent(json.getString("recentVersion"));
            mPolicyInfo.setDestVersions(json.getJSONArray("destVersions"));
            mPolicyInfo.setExcludeVersions(json.getJSONArray("excludeVersions"));
            mPolicyInfo.setPatchUrl(json.getString("patchUrl"));
            mPolicyInfo.setApkUrl(json.getString("apkUrl"));
            mPolicyInfo.setApkMD5(json.getString("apkMD5"));
            mPolicyInfo.setApkSize(json.getString("apkSize"));
            mPolicyInfo.setApkDate(json.getString("apkDate"));
        } catch (Exception e) {
            L.error(TAG, "policy info json parse error %s", e.toString());
            mPolicyInfo.reset();

            return false;
        }

        return true;
    }

    public boolean needUpgrade(Version version) {
        boolean old = version.smallThan(mPolicyInfo.mRecent);
        if (!old) {
            return false;
        }

        if (mPolicyInfo.mDestVersions.isEmpty()) {
            return true;
        }

        boolean dest = false;
        for (Pair<Version, Version> item : mPolicyInfo.mDestVersions) {
            if (inSection(version, item)) {
                dest = true;
                break;
            }
        }

        boolean exclude = false;
        for (Pair<Version, Version> item : mPolicyInfo.mExcludeVersions) {
            if (inSection(version, item)) {
                exclude = true;
                break;
            }
        }

        return dest && !exclude;
    }

    public boolean isIgnore() {
        if (Strength.Force == mPolicyInfo.mStrength) {
            return false;
        }

        if (0 >= mPolicyInfo.mPromptTime) {
            return false;
        }

        Version ver = UpgradeConfig.getIgnoreVersion();
        if (null == ver || !ver.equals(mPolicyInfo.mRecent)) {
            return false;
        }

        int promptTime = UpgradeConfig.getPromptTime();
        L.info(TAG, "ignore version %s, recent version %s, ignore time %d, prompt time %d"
                , ver.toString(), mPolicyInfo.mRecent.toString(), promptTime, mPolicyInfo.mPromptTime);

        return promptTime > mPolicyInfo.mPromptTime;
    }

    public void ignoreThisVersion() {
        int promptTime = UpgradeConfig.getPromptTime();
        Version ignore = UpgradeConfig.getIgnoreVersion();
        if (getRecent().equals(ignore)) {
            promptTime += 1;
        } else {
            UpgradeConfig.setIgnoreVersion(getRecent());
            promptTime = 1;
        }

        UpgradeConfig.savePromptTime(promptTime);
    }

    public String getApkUrl() {
        return mPolicyInfo.mApkUrl;
    }

    public String getPatchUrl() {
        return mPolicyInfo.mPatchUrl;
    }

    public String getApkMD5() {
        return mPolicyInfo.mApkMD5;
    }

    public Version getRecent() {
        return mPolicyInfo.mRecent;
    }

    public String getApkDate() {
        return mPolicyInfo.mApkDate;
    }

    public String getApkSize() {
        return mPolicyInfo.mApkSize;
    }

    public Strength getStrength() {
        return mPolicyInfo.mStrength;
    }

    private boolean inSection(Version version, Pair<Version, Version> section) {
        return (version.equals(section.first) || version.bigThan(section.first))
                && (version.equals(section.second) || version.smallThan(section.second));
    }

    private class PolicyInfo {
        private Strength mStrength;
        private int mPromptTime;
        private Version mRecent;
        private List<Pair<Version, Version>> mDestVersions;
        private List<Pair<Version, Version>> mExcludeVersions;
        private String mPatchUrl;
        private String mApkUrl;
        private String mApkMD5;
        private String mApkSize;
        private String mApkDate;

        public PolicyInfo() {
            mRecent = new Version();
            mDestVersions = new ArrayList<>();
            mExcludeVersions = new ArrayList<>();
            reset();
        }

        public void setStrength(String strength) {
            Strength[] values = Strength.values();
            for (Strength item : values) {
                if (0 == strength.compareTo(item.name())) {
                    mStrength = item;
                    break;
                }
            }
        }

        public void setPromptTime(int promptTime) {
            mPromptTime = promptTime;
        }

        public void setRecent(String recent) {
            mRecent = VersionUtils.getVerFromStr(recent);
        }

        public void setPatchUrl(String patchUrl) {
            mPatchUrl = patchUrl;
        }

        public void setApkUrl(String apkUrl) {
            mApkUrl = apkUrl;
        }

        public void setApkMD5(String md5) {
            mApkMD5 = md5;
        }

        public void setDestVersions(JSONArray jsonArray) throws JSONException {
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject json = jsonArray.getJSONObject(i);
                Version from = VersionUtils.getVerFromStr(json.getString("from"));
                Version to = VersionUtils.getVerFromStr(json.getString("to"));
                mDestVersions.add(new Pair<>(from, to));
            }
        }

        public void setExcludeVersions(JSONArray jsonArray) throws JSONException {
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject json = jsonArray.getJSONObject(i);
                Version from = VersionUtils.getVerFromStr(json.getString("from"));
                Version to = VersionUtils.getVerFromStr(json.getString("to"));
                mExcludeVersions.add(new Pair<>(from, to));
            }
        }

        public void setApkSize(String size) {
            mApkSize = size;
        }

        public void setApkDate(String date) {
            mApkDate = date;
        }

        public void reset() {
            mStrength = Strength.Normal;
            mPromptTime = 0;

            mRecent.mBuild = 0;
            mRecent.mMajor = 0;
            mRecent.mMinor = 0;

            mDestVersions.clear();
            mExcludeVersions.clear();

            mPatchUrl = "";
            mApkUrl = "";
            mApkMD5 = "";
            mApkSize = "";
            mApkDate = "";
        }

    }

}
