package com.x930073498.oar;

public interface FastHookCallback {
    void beforeHookedMethod(FastHookParam param);
    void afterHookedMethod(FastHookParam param);
}
