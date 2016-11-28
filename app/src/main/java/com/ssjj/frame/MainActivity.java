package com.ssjj.frame;

import android.os.Bundle;
import android.widget.TextView;

import com.ssjj.ioc.log.L;
import com.ssjj.ioc.ui.annotation.IAActivity;
import com.ssjj.ioc.ui.annotation.IAView;
import com.ssjj.ioc.ui.widget.AdaActivity;
import com.ssjj.ioc.ui.widget.AdaView;

@IAActivity(R.layout.activity_main)
public class MainActivity extends AdaActivity {
    private static  final String TAG="MainActivity";
    @IAView(R.id.tv_test)
    AdaView<TextView> tvTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTest.get().setText("test success");
        L.debug(TAG,"test");
    }
}
