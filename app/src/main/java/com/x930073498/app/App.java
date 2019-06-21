package com.x930073498.app;

import android.app.Application;
import android.util.Log;

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
        FastHookManager.doHook(MainActivity.class.getName(), null, "onResume", "V", new FastHookCallback() {
            @Override
            public void beforeHookedMethod(FastHookParam param) {
                Log.e("before","enter this line 1");
                Log.e("before param",""+param);
            }

            @Override
            public void afterHookedMethod(FastHookParam param) {
                Log.e("after","enter this line 2");
                Log.e("after param",""+param);
            }
        },FastHookManager.MODE_REPLACE,true);
    }
}
