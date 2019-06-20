package com.x930073498.island;

import android.content.Intent;

/**
 * Created by x930073498 on 2019/6/20.
 */
public class PermissionAction extends Action implements PermissionEvent {
    private static final String TAG = "PermissionAction";
    protected String[] permissions;

    private boolean hasRequest = false;

    PermissionAction(ActionDelegate delegate, ActionHandler handler, int requestCode, String[] permissions) {
        super(delegate, handler, requestCode);
        this.permissions = permissions;
    }

    @Override
    public void forEach(SinglePermissionCallback callback) {
        if (hasRequest) return;
        if (permissions == null || permissions.length == 0) return;
        Action action = new SinglePermissionAction(delegate, handler, requestCode, permissions, callback);
        handler.addAction(action);
        handler.request();
        hasRequest = true;
    }

    @Override
    public void forAll(MultiplePermissionCallback callback) {
        if (hasRequest) return;
        if (permissions == null || permissions.length == 0) return;
        Action action = new MultiplePermissionAction(delegate, handler, requestCode, permissions, callback);
        handler.addAction(action);
        handler.request();
        hasRequest = true;
    }

    @Override
    final void onActivityResult( int resultCode, Intent data) {
    }

}
