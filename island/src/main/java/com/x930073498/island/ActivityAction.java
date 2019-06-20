package com.x930073498.island;

import android.content.Intent;

import androidx.annotation.NonNull;

/**
 * Created by x930073498 on 2019/6/20.
 */
public class ActivityAction extends Action implements ActivityResultEvent {
    protected Intent intent;

    private boolean hasRequest = false;

    public ActivityAction(ActionDelegate delegate, ActionHandler handler, int requestCode, Intent intent) {
        super(delegate, handler, requestCode);
        this.intent = intent;
    }

    @Override
    public final void onResult(ActivityResultCallback callback) {
        if (hasRequest) return;
        if (intent == null) return;
        Action action = new ActivityActualAction(delegate, handler, requestCode, intent, callback);
        handler.addAction(action);
        handler.request();
        hasRequest = true;
    }

    @Override
    final void onRequestPermissionsResult( @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

}
