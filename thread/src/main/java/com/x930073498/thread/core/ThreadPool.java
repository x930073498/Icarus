package com.x930073498.thread.core;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.x930073498.thread.WindManager.backgroundService;
import static com.x930073498.thread.WindManager.mainService;

/**
 * Created by x930073498 on 2019/6/24.
 */
public class ThreadPool {


    private WeakReference<Object> ref;


    private static final List<ThreadPool> pools = new CopyOnWriteArrayList<>();

    private IExecutorService mMainService;
    private IExecutorService mBackgroundService;


    public synchronized static ThreadPool create(Object target) {
        ThreadPool pool = get(target);
        if (pool != null) return pool;
        pool = new ThreadPool(target);
        pools.add(0, pool);
        return pool;
    }


    public synchronized static boolean hasPool(Object target) {
        return get(target) != null;
    }

    public synchronized static ThreadPool get(Object target) {
        for (ThreadPool temp : pools) {
            if (temp.ref.get() == null) pools.remove(temp);
            else if (temp.ref.get() == target) {
                return temp;
            }
        }
        return null;
    }

    public IExecutorService getService(ThreadType type) {
        return type == ThreadType.background ? mBackgroundService : mMainService;
    }


    private ThreadPool(Object target) {
        ref = new WeakReference<>(target);
        mMainService = new DelegateExecutorService(this, mainService, ref);
        mBackgroundService = new DelegateExecutorService(this, backgroundService, ref);
    }

    public synchronized void clear() {
        pools.remove(this);
        ref.clear();
    }

}
