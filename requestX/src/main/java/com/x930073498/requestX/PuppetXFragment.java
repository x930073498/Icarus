package com.x930073498.requestX;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import com.x930073498.context.ContextManager;
import com.x930073498.context.State;
import com.x930073498.context.StateListener;
import com.x930073498.request.core.ActionDelegate;
import com.x930073498.request.core.ActionHandler;
import com.x930073498.request.core.Event;
import com.x930073498.request.permission.PermissionEvent;
import com.x930073498.request.result.ActivityResultEvent;

import static com.x930073498.request.RequestManager.TAG;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class PuppetXFragment extends Fragment implements Event, ActionDelegate, StateListener {

    private FragmentActivity activity;
    private ActionHandler handler = new ActionHandler(this);

    private boolean isAttached = false;

    public PuppetXFragment() {
        ContextManager.registerStateListener(this);
    }

    static PuppetXFragment get(FragmentActivity activity, String TAG) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(TAG);
        if (!(fragment instanceof PuppetXFragment)) {
            PuppetXFragment fragment1 = new PuppetXFragment();
            fragment1.activity = activity;
            return fragment1;
        } else {
            PuppetXFragment fragment1 = (PuppetXFragment) fragment;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        handler.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    public void onDestroy() {
        super.onDestroy();
        ContextManager.unregisterStateListener(this);
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
        requestPermissions(permissions, requestCode);
    }

    private boolean hasAdd = false;

    @Override
    public void attach() {
        if (isAttached) return;
        if (activity != null && !hasAdd) {
            hasAdd = true;
            activity.getSupportFragmentManager().beginTransaction().add(this, TAG).commitAllowingStateLoss();
        }
    }

    @Override
    public boolean isAttached() {
        return isAttached;
    }

    @Override
    public void onState(Activity activity, State state) {
        if (this.activity == activity) {
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
