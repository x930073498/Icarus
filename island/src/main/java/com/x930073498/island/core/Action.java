package com.x930073498.island.core;

import android.content.Intent;

import androidx.annotation.NonNull;

/**
 * Created by x930073498 on 2019/6/20.
 */
public abstract class Action {
    protected int requestCode;
    protected ActionHandler handler;
    protected ActionDelegate delegate;

    public Action(ActionDelegate delegate, ActionHandler handler, int requestCode) {
        this.requestCode = requestCode;
        this.handler = handler;
        this.delegate = delegate;
    }

    protected void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    protected void onActivityResult(int resultCode, Intent data) {

    }

    protected void requestInternal() {

    }
}
