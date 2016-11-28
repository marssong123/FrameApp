package com.ssjj.ioc.utils;

/**
 * Created by GZ1581 on 2016/5/20
 */

public final class Version {
    public int mMajor = 0;
    public int mMinor = 0;
    public int mBuild = 0;

    public boolean bigThan(Version v) {
        return (mMajor > v.mMajor) || ((mMajor == v.mMajor) && (mMinor > v.mMinor))
                || ((mMajor == v.mMajor) && (mMinor == v.mMinor) && (mBuild > v.mBuild));
    }

    public boolean smallThan(Version v) {
        return (mMajor < v.mMajor) || ((mMajor == v.mMajor) && (mMinor < v.mMinor))
                || ((mMajor == v.mMajor) && (mMinor == v.mMinor) && (mBuild < v.mBuild));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Version)) {
            return false;
        }

        Version v = (Version) o;
        return (mMajor == v.mMajor) && (mMinor == v.mMinor)
                && (mBuild == v.mBuild);
    }

    public String toString() {
        return String.format("%d.%d.%d", mMajor, mMinor, mBuild);
    }
}
