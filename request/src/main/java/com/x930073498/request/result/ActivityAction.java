package com.x930073498.request.result;

import android.content.Intent;


import com.x930073498.request.core.Action;
import com.x930073498.request.core.ActionDelegate;
import com.x930073498.request.core.ActionHandler;

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
        handler.requestActual();
        hasRequest = true;
    }

    @Override
   protected final void onRequestPermissionsResult( String[] permissions,int[] grantResults) {
    }

}
