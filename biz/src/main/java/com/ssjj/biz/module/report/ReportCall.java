package com.ssjj.biz.module.report;

import com.ssjj.ioc.event.signal.EventSignal;
import com.ssjj.biz.ui.widget.BizActivity;

/**
 * Created by GZ1581 on 2016/5/23
 */

public interface ReportCall {

    final class ActivityStatus extends EventSignal {
        BizActivity mActivity;
        boolean mResume;

        ActivityStatus(BizActivity activity, boolean resume) {
            mActivity = activity;
            mResume = resume;
        }
    }

    final class SignIn extends EventSignal {
        String mId;
        String mProvider;

        SignIn(String id, String provider) {
            mId = id;
            mProvider = provider;
        }
    }

    final class SignOff extends EventSignal {
        SignOff() {

        }
    }

    final class ReportEvent extends EventSignal {
        String mId;
        String mType;

        ReportEvent(String id) {
            this(id, null);
        }

        ReportEvent(String id, String type) {
            mId = id;
            mType = type;
        }
    }

    final class ReportError extends EventSignal {
        Throwable mThrowable;
        String mError;

        ReportError(Throwable throwable) {
            mThrowable = throwable;
        }

        ReportError(String error) {
            mError = error;
        }
    }
}
