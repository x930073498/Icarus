package com.x930073498.island.core;

import android.app.Activity;

import com.x930073498.island.IslandProvider;

/**
 * Created by x930073498 on 2019/6/20.
 */
public class DefaultIslandProvider implements IslandProvider {
    @Override
    public Event provide(Activity activity, String tag) {
        return PuppetFragment.get(activity, tag);
    }
}
