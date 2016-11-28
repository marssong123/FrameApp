package com.ssjj.mediasdk.videoplayer;

import java.util.Arrays;
import java.util.List;

/**
 * Created by GZ1581 on 2016/5/30
 */
public final class BlackList {

    private static final List<String> OverlayFormatFccES2 = Arrays.asList("ASUS_T00F");

    public static boolean isOverlayFormatFccES2Contain(String deviceName) {
        return OverlayFormatFccES2.contains(deviceName);
    }

}
