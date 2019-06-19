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


    private boolean isAttached = false;

    private AtomicInteger requestCodeGetter = new AtomicInteger();

    private ActionDelegate delegate;

    private volatile boolean isRequest = false;

    private List<Action> actions = Collections.synchronizedList(new ArrayList<Action>());

    private class Action implements PermissionAction, ActivityResultAction {

        private String[] permissions;
        private Intent intent;
        private ActivityResultCallback resultCallback;
        private SinglePermissionCallback singleCallback;
        private MultiplePermissionCallback multipleCallback;
        private int index = 0;


        private int requestCode;

        private boolean isPermissionCheck() {
            return intent == null;
        }

        private Action(int requestCode, String... permission) {
            this.permissions = permission;
            this.intent = null;
            this.requestCode = requestCode;
        }

        private Action(int requestCode, Intent intent) {
            this.intent = intent;
            this.permissions = null;
            this.requestCode = requestCode;
        }

        @Override
        public void onResult(ActivityResultCallback callback) {
            this.resultCallback = callback;
            if (!actions.contains(this))
                actions.add(this);
            request();
        }

        @Override
        public void forEach(SinglePermissionCallback callback) {
            singleCallback = callback;
            if (!actions.contains(this))
                actions.add(this);
            request();
        }

        @Override
        public void forAll(MultiplePermissionCallback callback) {
            multipleCallback = callback;
            if (!actions.contains(this))
                actions.add(this);
            request();
        }


        private void requestInternal() {
            if (isPermissionCheck()) {
                if (permissions.length <= 0) {
                    onRequestPermissionsResult(requestCode, new String[0], new int[0]);
                    return;
                }
                if (multipleCallback != null) {
                    delegate.requestPermission(requestCode, permissions);
                } else {
                    if (index >= permissions.length) {

                    }
                }

            } else {
                delegate.startResult(intent, requestCode);
            }
        }

        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            if (multipleCallback != null) {

            } else {
                if (index >= permissions.length) {
                    actions.remove(this);
                    isRequest = false;
                    request();
                } else {
                    requestInternal();
                }
            }

        }

        void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCallback != null) {
                resultCallback.call(requestCode, resultCode, data);
            }
            actions.remove(this);
            isRequest = false;
            request();
        }

    }

    public void setAttached(boolean attached) {
        isAttached = attached;
    }

    ActionHandler(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Action action = getAction(requestCode);
        if (action != null) {
            action.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        Action action = getAction(requestCode);
        if (action != null) {
            action.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Action getAction(int requestCode) {
        for (Action action : actions
        ) {
            if (action != null && action.requestCode == requestCode) return action;
        }
        return null;
    }

    void request() {
        if (!isAttached) return;
        if (isRequest) return;
        if (actions.isEmpty()) return;
        isRequest = true;
        Action action = actions.get(0);
        action.requestInternal();
    }


    @Override
    public PermissionAction request(String... permission) {
        return new Action(requestCodeGetter.incrementAndGet(), permission);
    }

    @Override
    public ActivityResultAction request(Intent intent) {
        return new Action(requestCodeGetter.incrementAndGet(), intent);

    }
}
