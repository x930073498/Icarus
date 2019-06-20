package com.x930073498.island.core;

import android.content.Intent;

import com.x930073498.island.permission.PermissionEvent;
import com.x930073498.island.result.ActivityResultEvent;

/**
 * Created by x930073498 on 2019/6/19.
 */
public interface Event {

    PermissionEvent request(String... permission);

    ActivityResultEvent request(Intent intent);

}
