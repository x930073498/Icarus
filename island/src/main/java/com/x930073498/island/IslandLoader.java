package com.x930073498.island;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;

import com.x930073498.boat.BoatManager;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class IslandLoader {

    public static final String tag = System.currentTimeMillis() + "IslandLoader";

    public static Event guess() {
        return attach(BoatManager.getTopActivity());
    }

    public static Event attach(Activity activity) {
        if (activity == null) throw new RuntimeException("activity not found");
        if (activity instanceof FragmentActivity) {
            return new PuppetXFragment();
        } else {
            return new PuppetFragment();
        }
    }

}
