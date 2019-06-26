package com.x930073498.wind.core;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

class SettableFutureTask extends FutureTask<Object> {
    SettableFutureTask() {
        super(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        });
    }

    @Override
    public void set(Object o) {
        super.set(o);
    }
}