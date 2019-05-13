package io.agora.tutorials1v1acall;

import android.app.Application;

import com.huiwang.HWCommunication;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HWCommunication.init(this,"VB0m9Ew1rEcOhoi_9*kl","21001435");
    }
}
