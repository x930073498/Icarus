package com.x930073498.wind.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by x930073498 on 2019/6/24.
 */
public  class DelegateExecutorService implements IExecutorService {

    private ExecutorService service;
    private WeakReference<Object> ref;

    private ThreadPool pool;


    public DelegateExecutorService(ThreadPool pool, ExecutorService service, WeakReference<Object> ref) {
        this.service = service;
        this.ref = ref;
        this.pool = pool;
    }

    @Override
    public void shutdown() {
        service.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return service.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return service.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return service.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return service.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (ref == null || ref.get() == null) return null;
        return service.submit(new WeakCallable<>(ref, task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if (ref == null || ref.get() == null) return null;
        return service.submit(new WeakRunnable(ref, task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (ref == null || ref.get() == null) return null;
        return service.submit(new WeakRunnable(ref, task));
    }

    private <T> Collection<? extends Callable<T>> toWeak(Collection<? extends Callable<T>> tasks) {
        List<Callable<T>> result = new ArrayList<>();
        for (Callable<T> callable : tasks
        ) {
            result.add(new WeakCallable<>(ref, callable));
        }
        return result;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        if (ref == null || ref.get() == null) return null;
        return service.invokeAll(toWeak(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        if (ref == null || ref.get() == null) return null;
        return service.invokeAll(toWeak(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
        if (ref == null || ref.get() == null) return null;
        return service.invokeAny(toWeak(tasks));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        if (ref == null || ref.get() == null) return null;
        return service.invokeAny(toWeak(tasks), timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        if (ref == null || ref.get() == null) return;
        service.execute(new WeakRunnable(ref, command));
    }

    public void cancel() {
        pool.clear();
    }

}
