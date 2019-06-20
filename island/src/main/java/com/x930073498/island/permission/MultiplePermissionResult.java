package com.x930073498.island.permission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class MultiplePermissionResult {
     boolean granted=false;

     List<Permission> permissions=new ArrayList<>();

    public boolean isGranted() {
        return granted;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public String toString() {
        return "MultiplePermissionResult{" +
                "granted=" + granted +
                ", permissions=" + permissions +
                '}';
    }
}
