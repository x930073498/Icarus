package com.x930073498.island.permission;

import android.content.pm.PackageManager;


import com.x930073498.island.core.ActionDelegate;
import com.x930073498.island.core.ActionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x930073498 on 2019/6/20.
 */
class MultiplePermissionAction extends PermissionAction {
    private MultiplePermissionCallback callback;

    MultiplePermissionAction(ActionDelegate delegate, ActionHandler handler, int requestCode, String[] permissions, MultiplePermissionCallback callback) {
        super(delegate, handler, requestCode, permissions);
        this.callback = callback;
    }

    @Override
    protected void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
        if (callback != null) {
            MultiplePermissionResult result = new MultiplePermissionResult();
            boolean isAllGranted = true;
            Permission permission;
            for (int i = 0; i < permissions.length; i++) {
                permission = new Permission();
                permission.name = permissions[i];
                permission.shouldShowRequestPermissionRationale = delegate.shouldShowRequestPermissionRationale(permission.name);
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    isAllGranted = false;
                    permission.granted = false;
                } else {
                    permission.granted = true;
                }
                result.permissions.add(permission);
            }
            result.granted = isAllGranted;
            callback.call(result);
        }
        handler.removeAction(this);
        handler.setRequest(false);
        handler.requestActual();
    }

    @Override
    protected void requestInternal() {
        delegate.requestPermission(requestCode, permissions);
    }
}
