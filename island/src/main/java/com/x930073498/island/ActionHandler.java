package com.x930073498.island;

import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class ActionHandler implements Event {

    private volatile boolean isActive = true;
    private static AtomicInteger requestCodeGetter = new AtomicInteger(10000);
    private List<Action> actions = Collections.synchronizedList(new ArrayList<Action>());

    private ActionDelegate delegate;

    private volatile boolean isRequest = false;

    void onResume() {
        isActive = true;
        request();
    }

    void onPause() {
        isActive = false;
    }

    void onDestroy() {
        isActive = false;
        actions.clear();
    }


    void setRequest(boolean request) {
        isRequest = request;
    }

    ActionHandler(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Action action = getAction(requestCode);
        if (action != null) {
            action.onRequestPermissionsResult(permissions, grantResults);
        }
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        Action action = getAction(requestCode);
        if (action != null) action.onActivityResult(resultCode, data);

    }

    synchronized void addAction(Action action) {
        if (actions.contains(action)) return;
        actions.add(action);
    }

    synchronized void removeAction(Action action) {
        actions.remove(action);
    }

    private Action getAction(int requestCode) {
        for (Action action : actions
        ) {
            if (action != null && action.requestCode == requestCode) return action;
        }
        return null;
    }

    void request() {
        if (!delegate.isAttached()) {
            delegate.attach();
            return;
        }
        if (isRequest) return;
        if (!isActive) return;
        if (actions.isEmpty()) return;
        isRequest = true;
        Action action = actions.get(0);
        action.requestInternal();
    }


    @Override
    public PermissionEvent request(String... permission) {
        return new PermissionAction(delegate, this, requestCodeGetter.incrementAndGet(), permission);
    }

    @Override
    public ActivityResultEvent request(Intent intent) {
        return new ActivityAction(delegate, this, requestCodeGetter.incrementAndGet(), intent);

    }
}
