package com.x930073498.island.core;

import android.content.Intent;

/**
 * Created by x930073498 on 2019/6/19.
 */
public interface ActionDelegate {
    void startResult(Intent intent, int requestCode);

    void requestPermission(int requestCode, String... permissions);

    void attach();

    boolean isAttached();
}
