package com.x930073498.island;

import android.app.Activity;

import com.x930073498.island.core.Event;


/**
 * Created by x930073498 on 2019/6/20.
 */
public interface IslandProvider {
    Event provide(Activity activity, String tag);
}
