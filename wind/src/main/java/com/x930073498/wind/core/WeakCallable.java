package com.x930073498.wind.core;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

/**
 * Created by x930073498 on 2019/6/24.
 */
public class WeakCallable<V> implements Callable<V> {

    private WeakReference<Callable<V>> callableRef;
    private WeakReference<Object> ref;

    public WeakCallable(WeakReference<Object> ref, Callable<V> callable) {
        this.ref = ref;
        this.callableRef = new WeakReference<>(callable);
    }

    @Override
    public V call() throws Exception {
        if (ref == null || ref.get() == null || callableRef == null) return null;
        Callable<V> callable = callableRef.get();
        return callable == null ? null : callable.call();
    }
}
