package com.ssjj.biz.module.upgrade;

import com.ssjj.ioc.event.signal.EventSignal;

/**
 * Created by GZ1581 on 2016/6/8
 */


public interface UpgradeInterface {
    final class CheckUpgrade extends EventSignal {
        public CheckUpgrade() {
        }

    }

    final class InstallUpgrade extends EventSignal {
        int mAppLogoResId;

        public InstallUpgrade(int appLogoResId) {
            mAppLogoResId = appLogoResId;
        }
    }

    final class IgnoreUpgrade extends EventSignal {
        public IgnoreUpgrade() {

        }
    }
}
