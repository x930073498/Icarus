package com.x930073498.island;

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

    void onRequestPermissionsResult( @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    void onActivityResult( int resultCode, Intent data) {

    }

     void requestInternal(){

     }
}
