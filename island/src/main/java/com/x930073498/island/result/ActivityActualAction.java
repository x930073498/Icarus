package com.x930073498.island.result;

import android.content.Intent;

import com.x930073498.island.core.ActionDelegate;
import com.x930073498.island.core.ActionHandler;

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
   protected void onActivityResult( int resultCode, Intent data) {
        callback.call( resultCode, data);
        handler.removeAction(this);
        handler.setRequest(false);
        handler.request();
    }

    @Override
   protected void requestInternal() {
        delegate.startResult(intent, requestCode);
    }
}
