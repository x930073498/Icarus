package com.x930073498.island;

import android.app.Activity;

import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.x930073498.boat.BoatManager;
import com.x930073498.boat.State;
import com.x930073498.boat.StateListener;
import com.x930073498.island.core.Event;
import com.x930073498.island.core.PuppetFragment;
import com.x930073498.island.core.PuppetXFragment;

import java.util.Collections;
import java.util.Map;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class IslandLoader {

    public static final String TAG = System.currentTimeMillis() + "IslandLoader";
    private static final Map<Activity, Event> map = Collections.synchronizedMap(new ArrayMap<Activity, Event>());

    public static Event guess() {
        return attach(BoatManager.getTopActivity());
    }

    static {
        BoatManager.registerStateListener(new StateListener() {
            @Override
            public void onState(Activity activity, State state) {
                if (state == State.DESTROYED) {
                    map.remove(activity);
                }
            }
        });
    }


    public synchronized static Event attach(Activity activity) {
        if (activity == null) throw new RuntimeException("activity not found");
        Event event = map.get(activity);
        if (event != null) return event;
        if (activity instanceof FragmentActivity) {
            Fragment fragment = ((FragmentActivity) activity).getSupportFragmentManager().findFragmentByTag(TAG);
            if (!(fragment instanceof PuppetXFragment)) {
                PuppetXFragment fragment1 = new PuppetXFragment();
                fragment1.activity = activity;
                map.put(activity, fragment1);
                return fragment1;
            } else {
                PuppetXFragment fragment1 = (PuppetXFragment) fragment;
                fragment1.activity = activity;
                map.put(activity, fragment1);
                return fragment1;
            }

        } else {
            android.app.Fragment fragment = activity.getFragmentManager().findFragmentByTag(TAG);
            if (!(fragment instanceof PuppetFragment)) {
                PuppetFragment fragment1 = new PuppetFragment();
                fragment1.activity = activity;
                map.put(activity, fragment1);
                return fragment1;
            } else {
                PuppetFragment fragment1 = (PuppetFragment) fragment;
                fragment1.activity = activity;
                map.put(activity, fragment1);
                return fragment1;
            }

        }
    }

}
