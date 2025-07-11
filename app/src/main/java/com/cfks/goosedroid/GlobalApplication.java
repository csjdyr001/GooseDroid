package com.cfks.goosedroid;

import android.app.Application;
import android.widget.Toast;
import com.yc.toollib.tool.*;
import com.yc.toollib.crash.*;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Implement this method
        CrashHandler.getInstance().init(this, new CrashListener() {
            //重启app
            @Override
            public void againStartApp() {
                CrashToolUtils.startCrashListActivity(GlobalApplication.this);
            }
            //自定义上传crash，支持开发者上传自己捕获的crash数据
            @Override
            public void recordException(Throwable ex) {
                //自定义上传crash，支持开发者上传自己捕获的crash数据
                //StatService.recordException(getApplication(), ex);
            }
        });
    }
}