package com.x930073498.island;

/**
 * Created by x930073498 on 2019/6/19.
 */
public interface PermissionAction {
    void forEach(SinglePermissionCallback callback);

    void forAll(MultiplePermissionCallback callback);

}
