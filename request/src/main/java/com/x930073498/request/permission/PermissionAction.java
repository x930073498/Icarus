package com.x930073498.request.permission;

import android.content.Intent;

import com.x930073498.request.core.Action;
import com.x930073498.request.core.ActionDelegate;
import com.x930073498.request.core.ActionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x930073498 on 2019/6/20.
 */
public class PermissionAction extends Action implements PermissionEvent {
    protected String[] permissions;

    private boolean hasRequest = false;

    public PermissionAction(ActionDelegate delegate, ActionHandler handler, int requestCode, String[] permissions) {
        super(delegate, handler, requestCode);
        this.permissions = permissions;
        checkPermissions();
    }

    private void checkPermissions() {
        List<String> list = new ArrayList<>();
        for (String permission : permissions
        ) {
            if (Permission.isValid(permission)) list.add(permission);
        }
        permissions = list.toArray(new String[0]);
    }

    @Override
    public void forEach(SinglePermissionCallback callback) {
        if (hasRequest) return;
        if (permissions == null || permissions.length == 0) return;
        Action action = new SinglePermissionAction(delegate, handler, requestCode, permissions, callback);
        handler.addAction(action);
        handler.requestActual();
        hasRequest = true;
    }

    @Override
    public void forAll(MultiplePermissionCallback callback) {
        if (hasRequest) return;
        if (permissions == null || permissions.length == 0) return;
        Action action = new MultiplePermissionAction(delegate, handler, requestCode, permissions, callback);
        handler.addAction(action);
        handler.requestActual();
        hasRequest = true;
    }

    @Override
    protected final void onActivityResult(int resultCode, Intent data) {
    }

}
