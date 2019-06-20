package com.x930073498.island;

import android.content.Intent;

/**
 * Created by x930073498 on 2019/6/20.
 */
class ActivityActualAction extends ActivityAction {
    private ActivityResultCallback callback;

    ActivityActualAction(ActionDelegate delegate, ActionHandler handler, int requestCode, Intent intent, ActivityResultCallback callback) {
        super(delegate, handler, requestCode, intent);
        this.callback = callback;
    }

    @Override
    void onActivityResult( int resultCode, Intent data) {
        callback.call( resultCode, data);
        handler.removeAction(this);
        handler.setRequest(false);
        handler.request();
    }

    @Override
    void requestInternal() {
        delegate.startResult(intent, requestCode);
    }
}
