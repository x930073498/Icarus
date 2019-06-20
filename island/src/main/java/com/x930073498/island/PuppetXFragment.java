package com.x930073498.island;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.x930073498.boat.BoatManager;
import com.x930073498.boat.State;
import com.x930073498.boat.StateListener;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class PuppetXFragment extends Fragment implements Event, ActionDelegate, StateListener {

    Activity activity;
    private ActionHandler handler = new ActionHandler(this);

    private boolean isAttached = false;

    public PuppetXFragment() {
        BoatManager.registerStateListener(this);
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
        handler.request();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BoatManager.unregisterStateListener(this);
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
        if (activity instanceof FragmentActivity && !hasAdd) {
            hasAdd = true;
            ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction().add(this, IslandLoader.TAG).commitAllowingStateLoss();
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
            }else if (state==State.DESTROYED){
                handler.onDestroy();
            }
        }
    }
}
