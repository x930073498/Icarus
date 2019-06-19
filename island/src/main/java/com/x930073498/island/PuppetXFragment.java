package com.x930073498.island;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class PuppetXFragment extends Fragment implements Event, ActionDelegate {


    private ActionHandler handler = new ActionHandler(this);


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        handler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        handler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public PermissionAction request(String... permission) {
        return handler.request(permission);
    }

    @Override
    public ActivityResultAction request(Intent intent) {
        return handler.request(intent);
    }

    @Override
    public void startResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void requestPermission(int requestCode, String... permissions) {
        requestPermissions(permissions, requestCode);
    }
}
