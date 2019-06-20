package com.x930073498.island.permission;

/**
 * Created by x930073498 on 2019/6/19.
 */
public interface PermissionEvent {
    void forEach(SinglePermissionCallback callback);

    void forAll(MultiplePermissionCallback callback);

}
