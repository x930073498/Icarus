package com.x930073498.island;

import android.content.Intent;

/**
 * Created by x930073498 on 2019/6/19.
 */
public interface Event {

    PermissionAction request(String... permission);

    ActivityResultAction request(Intent intent);

}
