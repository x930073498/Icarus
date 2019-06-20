package com.x930073498.islandx;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;

import com.x930073498.island.core.IslandProvider;
import com.x930073498.island.core.Event;

/**
 * Created by x930073498 on 2019/6/20.
 */
public class IslandXProvider implements IslandProvider {

    @Override
    public Event provide(Activity activity, String tag) {
        if (activity instanceof FragmentActivity)
            return PuppetXFragment.get((FragmentActivity) activity, tag);
        return null;
    }
}
