package com.x930073498.island.event;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.x930073498.boat.BoatManager;
import com.x930073498.boat.State;
import com.x930073498.boat.StateListener;
import com.x930073498.island.IslandManager;
import com.x930073498.island.core.ActionDelegate;
import com.x930073498.island.core.ActionHandler;
import com.x930073498.island.core.Event;
import com.x930073498.island.permission.PermissionEvent;
import com.x930073498.island.result.ActivityResultEvent;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class PuppetFragment extends Fragment implements Event, ActionDelegate, StateListener {


    private Activity activity;
    private ActionHandler handler = new ActionHandler(this);
    private boolean isAttached = false;

    public PuppetFragment() {
        BoatManager.registerStateListener(this);
    }

    public static PuppetFragment get(Activity activity,  String TAG) {
        Fragment fragment = activity.getFragmentManager().findFragmentByTag(TAG);
        if (!(fragment instanceof PuppetFragment)) {
            PuppetFragment fragment1 = new PuppetFragment();
            fragment1.activity = activity;
            return fragment1;
        } else {
            PuppetFragment fragment1 = (PuppetFragment) fragment;
            fragment1.activity = activity;
            return fragment1;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
        handler.requestActual();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        handler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public PermissionEvent request(String... permission) {
        return handler.request(permission);
    }

    @Override
    public ActivityResultEvent request(Intent intent) {
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

    private boolean hasAdded = false;

    @Override
    public void attach() {
        if (!isAttached && !hasAdded) {
            hasAdded = true;
            activity.getFragmentManager().beginTransaction()
                    .add(this, IslandManager.TAG)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public boolean isAttached() {
        return isAttached;
    }

    @Override
    public void onState(Activity activity, State state) {
        if (activity == this.activity) {
            if (state == State.RESUMED) {
                handler.onResume();
            } else if (state == State.PAUSED) {
                handler.onPause();
            } else if (state == State.DESTROYED) {
                handler.onDestroy();
            }
        }
    }
}
