package com.x930073498.island.result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.x930073498.boat.BoatManager;
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
    protected void onActivityResult(int resultCode, Intent data) {
        callback.call(resultCode, data);
        handler.removeAction(this);
        handler.setRequest(false);
        handler.requestActual();
    }

    @Override
    protected void requestInternal() {
        PackageManager pm = BoatManager.getApplicationContext().getPackageManager();
        ResolveInfo info = pm.resolveActivity(intent, 0);
        if (info == null) {
            onActivityResult(Activity.RESULT_CANCELED, null);
        } else
            delegate.startResult(intent, requestCode);
    }
}
