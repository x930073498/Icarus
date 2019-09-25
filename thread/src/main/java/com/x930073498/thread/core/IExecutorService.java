package com.x930073498.thread.core;

import java.util.concurrent.ExecutorService;

/**
 * Created by x930073498 on 2019/6/24.
 */
public interface  IExecutorService extends ExecutorService {

    void cancel();

}
