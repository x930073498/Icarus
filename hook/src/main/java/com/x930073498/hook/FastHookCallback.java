package com.x930073498.hook;

public interface FastHookCallback {
    void beforeHookedMethod(FastHookParam param);
    void afterHookedMethod(FastHookParam param);
}
