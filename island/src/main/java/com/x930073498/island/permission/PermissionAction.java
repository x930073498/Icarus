package com.x930073498.island.permission;

import android.content.Intent;

import com.x930073498.island.core.Action;
import com.x930073498.island.core.ActionDelegate;
import com.x930073498.island.core.ActionHandler;

/**
 * Created by x930073498 on 2019/6/20.
 */
public class PermissionAction extends Action implements PermissionEvent {
    private static final String TAG = "PermissionAction";
    protected String[] permissions;

    private boolean hasRequest = false;

    public PermissionAction(ActionDelegate delegate, ActionHandler handler, int requestCode, String[] permissions) {
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
   protected final void onActivityResult( int resultCode, Intent data) {
    }

}
