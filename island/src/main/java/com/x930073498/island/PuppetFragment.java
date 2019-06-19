package com.x930073498.island;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class PuppetFragment extends Fragment implements Event, ActionDelegate {


    private ActionHandler handler = new ActionHandler(this);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        handler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        handler.setAttached(true);
        handler.request();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler.setAttached(false);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        } else {
            int[] result = new int[permissions.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = PackageManager.PERMISSION_GRANTED;
            }
            handler.onRequestPermissionsResult(requestCode, permissions, result);
        }
    }
}
