package com.x930073498.island;

/**
 * Created by x930073498 on 2019/6/20.
 */
public class Permission {
     String name;
     boolean granted;
     boolean shouldShowRequestPermissionRationale;

    public String getName() {
        return name;
    }

    public boolean isGranted() {
        return granted;
    }

    public boolean isShouldShowRequestPermissionRationale() {
        return shouldShowRequestPermissionRationale;
    }
}
