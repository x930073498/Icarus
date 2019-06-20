package com.x930073498.island;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

/**
 * Created by x930073498 on 2019/6/20.
 */
class MultiplePermissionAction extends PermissionAction {
    private static final String TAG = "MultiplePermissionAction";
    private MultiplePermissionCallback callback;

    MultiplePermissionAction(ActionDelegate delegate, ActionHandler handler, int requestCode, String[] permissions, MultiplePermissionCallback callback) {
        super(delegate, handler, requestCode, permissions);
        this.callback = callback;
    }

    @Override
    void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
        if (callback != null) {
            MultiplePermissionResult result = new MultiplePermissionResult();
            boolean isAllGranted = true;
            Permission permission;
            for (int i = 0; i < permissions.length; i++) {
                permission = new Permission();
                permission.name = permissions[i];
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
        handler.request();
    }

    @Override
    void requestInternal() {
        delegate.requestPermission(requestCode, permissions);
    }
}
