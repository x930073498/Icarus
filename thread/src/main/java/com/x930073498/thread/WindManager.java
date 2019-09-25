package com.x930073498.thread;

import com.x930073498.thread.core.IExecutorService;
import com.x930073498.thread.core.MainHandlerExecutorService;
import com.x930073498.thread.core.ThreadPool;
import com.x930073498.thread.core.ThreadType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by x930073498 on 2019/6/24.
 * <p>
 * 线程相关
 */
public class WindManager {

    private static final int TAG = 0;
    public static MainHandlerExecutorService mainService = new MainHandlerExecutorService();

    public static ExecutorService backgroundService = Executors.newCachedThreadPool();

    public static IExecutorService getMainExecutorService(Object target) {
        ThreadPool pool = ThreadPool.create(target);
        return pool.getService(ThreadType.main);
    }

    public static IExecutorService getBackgroundExecutorService(Object target) {
        ThreadPool pool = ThreadPool.create(target);
        return pool.getService(ThreadType.background);
    }

    public static void cancel(Object target) {
        ThreadPool pool = ThreadPool.get(target);
        if (pool == null) return;
        pool.clear();
    }

}
