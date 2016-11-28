package com.ssjj.ioc.utils;

/**
 * Created by GZ1581 on 2016/6/8
 */

public final class NotifyUtils {
    private static final int UpgradeId = 1;
    private static int GlobalIDStart = 3;

    public static NotifyID genGlobalNotifyID() {
        GlobalIDStart += 1;
        if (Integer.MAX_VALUE <= GlobalIDStart) {
            GlobalIDStart = 3;
        }

        return new NotifyID(GlobalIDStart);
    }

    public static NotifyID getUpgradeNotifyID() {
        return new NotifyID(UpgradeId);
    }

    public static class NotifyID {
        private int mID;

        private NotifyID(int id) {
            mID = id;
        }

        public int getID() {
            return mID;
        }
    }
}
