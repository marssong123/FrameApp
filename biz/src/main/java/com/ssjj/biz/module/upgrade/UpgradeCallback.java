package com.ssjj.biz.module.upgrade;

import com.ssjj.ioc.event.signal.EventSignal;

/**
 * Created by GZ1581 on 2016/6/6
 */

public interface UpgradeCallback {
    final class UpgradeArrived extends EventSignal {
        UpgradeArrived() {

        }
    }

    final class RecentVersion extends EventSignal {
        RecentVersion() {

        }
    }
}
