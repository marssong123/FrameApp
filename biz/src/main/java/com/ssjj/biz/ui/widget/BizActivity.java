package com.ssjj.biz.ui.widget;

import com.ssjj.ioc.ui.widget.AdaActivity;
import com.ssjj.biz.module.report.Report;

/**
 * Created by GZ1581 on 2016/5/19
 */

public class BizActivity extends AdaActivity {

    @Override
    protected void onResume() {
        super.onResume();

        Report.activityStatus(this, true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Report.activityStatus(this, false);
    }
}
