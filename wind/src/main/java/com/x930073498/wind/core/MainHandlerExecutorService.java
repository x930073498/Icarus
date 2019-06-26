package com.x930073498.wind.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class MainHandlerExecutorService extends AbstractExecutorService implements Handler.Callback {

    public Handler handler = new Handler(Looper.getMainLooper(), this);
    private final ConcurrentHashMap<Integer, Runnable> mRemainingTasks = new ConcurrentHashMap<>();
    private final AtomicInteger mTaskId = new AtomicInteger();

    private final SettableFutureTask mQuitPromise = new SettableFutureTask();
    private volatile boolean mShouldShutdown = false;

    private static final int RUN_MESSAGE = 1;
    private static final int SHUTDOWN_NOW_MESSAGE = 2;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case RUN_MESSAGE:
                final Runnable task = mRemainingTasks.remove(msg.arg1);
                if (task != null)
                    task.run();
                if (mShouldShutdown && mRemainingTasks.isEmpty()) {
                    terminate();
                }
                break;

            case SHUTDOWN_NOW_MESSAGE:
                terminate();
                break;
        }
        return true;
    }

    private void terminate() {
        handler.getLooper().quit();
        mQuitPromise.set(null);
    }

    @Override
    public void shutdown() {
        mShouldShutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        mShouldShutdown = true;
        List<Runnable> remainingTasks = Collections.unmodifiableList(new ArrayList<>(mRemainingTasks.values()));
        mRemainingTasks.clear();
        handler.getLooper().getThread().interrupt();
        handler.sendEmptyMessage(SHUTDOWN_NOW_MESSAGE);
        return remainingTasks;
    }

    @Override
    public boolean isShutdown() {
        return mShouldShutdown;
    }

    @Override
    public boolean isTerminated() {
        return mQuitPromise.isDone();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            mQuitPromise.get(timeout, unit);
            return true;
        } catch (final ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        } catch (final TimeoutException e) {
            return false;
        }
    }

    @Override
    public void execute(Runnable command) {
        if (mShouldShutdown) {
            throw new RejectedExecutionException();
        }
        final int taskId = mTaskId.getAndIncrement();
        mRemainingTasks.put(taskId, command);
        Message message = handler.obtainMessage(RUN_MESSAGE, taskId, 0);
        handler.sendMessage(message);
    }
}