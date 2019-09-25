package com.x930073498.request.result;

import android.content.Intent;

/**
 * Created by x930073498 on 2019/6/19.
 */
public interface ActivityResultCallback {
    void call( int resultCode, Intent data);
}
