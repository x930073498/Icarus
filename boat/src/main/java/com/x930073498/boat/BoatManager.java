package com.x930073498.boat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.collection.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BoatManager {
    private static Application mApp;

    private static List<Activity> activities = Collections.synchronizedList(new ArrayList<Activity>());
    private static Map<Activity, State> map = Collections.synchronizedMap(new ArrayMap<Activity, State>());

    public static State getState(Activity activity) {
        State state = map.get(activity);
        return state == null ? State.DESTROYED : state;
    }

    public static Activity getTopActivity() {
        if (activities.isEmpty()) return null;
        return activities.get(0);
    }
    public static State getTopState(){
        return getState(getTopActivity());
    }

    public static boolean isInForground() {
        Activity activity = getTopActivity();
        return isActive(getState(activity));
    }

    public static void exit() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }

    private static boolean isActive(State state) {
        return state == State.CREATED || state == State.RESUMED;
    }

    public synchronized static void init(Application app) {
        if (app == null) return;
        if (mApp != null) return;
        mApp = app;
        mApp.registerActivityLifecycleCallbacks(new Callback());
    }

    public static Context getApplicationContext() {
        return mApp;
    }

    public static <T extends Application> T getApplication() {
        return (T) mApp;
    }

    public static Context getLatestContext() {
        Activity activity = getTopActivity();
        if (activity == null) return mApp;
        return activity;
    }

    private static class Callback implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activities.add(0, activity);
            map.put(activity, State.CREATED);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            map.put(activity, State.STARTED);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            map.put(activity, State.RESUMED);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            map.put(activity, State.PAUSED);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            map.put(activity, State.STOPPED);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            map.put(activity, State.SAVE_INSTANCE_STATE);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            activities.remove(activity);
            map.remove(activity);
        }
    }

}
