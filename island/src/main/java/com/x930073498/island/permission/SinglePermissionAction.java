package com.x930073498.island.permission;

import android.content.pm.PackageManager;


import com.x930073498.island.core.ActionDelegate;
import com.x930073498.island.core.ActionHandler;

/**
 * Created by x930073498 on 2019/6/20.
 */
class SinglePermissionAction extends PermissionAction {
    private SinglePermissionCallback callback;

    private int index = 0;


    SinglePermissionAction(ActionDelegate delegate, ActionHandler handler, int requestCode, String[] permissions, SinglePermissionCallback callback) {
        super(delegate, handler, requestCode, permissions);
        this.callback = callback;
    }

    @Override
  protected   void onRequestPermissionsResult(  String[] permissions, int[] grantResults) {
        if (permissions.length > 0) {
            SinglePermissionResult result = new SinglePermissionResult();
            result.name = permissions[0];
            result.granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean flag = callback.intercept(result);
            if (flag) {
                finishRequest();
            } else {
                index++;
                if (index >= this.permissions.length) {
                    finishRequest();
                } else {
                    requestInternal();
                }
            }
        } else {
            finishRequest();
        }
    }

    private void finishRequest() {
        handler.removeAction(this);
        handler.setRequest(false);
        handler.requestActual();
    }

    @Override
   protected void requestInternal() {
        delegate.requestPermission(requestCode, permissions[index]);
    }
}
