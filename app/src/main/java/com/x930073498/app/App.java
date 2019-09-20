package com.x930073498.app;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.squareup.leakcanary.AndroidRefWatcherBuilder;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcherBuilder;
import com.x930073498.oar.FastHookCallback;
import com.x930073498.oar.FastHookManager;
import com.x930073498.oar.FastHookParam;

/**
 * Created by x930073498 on 2019/6/21.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            //You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        FastHookManager.doHook(Activity.class.getName(), null, "onResume", "V", new FastHookCallback() {
            @Override
            public void beforeHookedMethod(FastHookParam param) {
            }

            @Override
            public void afterHookedMethod(FastHookParam param) {
                Log.e("after","enter this line 2");
                Log.e("after param",""+param);
            }
        },FastHookManager.MODE_REWRITE,true);
    }
}
