package com.x930073498.request.event;

import android.app.Activity;

import com.x930073498.request.core.Event;
import com.x930073498.request.core.IslandProvider;

/**
 * Created by x930073498 on 2019/6/20.
 */
public class DefaultIslandProvider implements IslandProvider {
    @Override
    public Event provide(Activity activity, String tag) {
        return PuppetFragment.get(activity, tag);
    }
}
