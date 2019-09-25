package com.x930073498.request.permission;

/**
 * Created by x930073498 on 2019/6/19.
 */
public interface SinglePermissionCallback {

    //返回true表示拦截，不进行下一步的请求
    boolean intercept(SinglePermissionResult result);
}
