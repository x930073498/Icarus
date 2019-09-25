package com.x930073498.thread.core;

import java.lang.ref.WeakReference;

/**
 * Created by x930073498 on 2019/6/24.
 */
 class WeakRunnable implements Runnable {
    private WeakReference<Runnable> runnableRef;
    private WeakReference<Object> targetRef;

    WeakRunnable(WeakReference<Object> target, Runnable runnable) {
        runnableRef = new WeakReference<>(runnable);
        targetRef = target;
    }

    @Override
    public void run() {
        Object target = targetRef.get();
        if (target == null) return;
        Runnable runnable = runnableRef.get();
        if (runnable == null) return;
        runnable.run();
    }
}
