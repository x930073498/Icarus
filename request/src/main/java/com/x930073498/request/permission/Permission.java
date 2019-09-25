package com.x930073498.request.permission;

import android.Manifest;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by x930073498 on 2019/6/20.
 */
public class Permission {
    private static final List<String> permissions = new ArrayList<>();

    static {
        Field[] fields = Manifest.permission.class.getDeclaredFields();
        for (Field field : fields
        ) {
            if (field.getType() == String.class) {
                try {
                    Object value = field.get(null);
                    if (value != null) {
                        permissions.add(value.toString());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isValid(String permission) {
        return !TextUtils.isEmpty(permission) && permissions.contains(permission);
    }

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

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", granted=" + granted +
                ", shouldShowRequestPermissionRationale=" + shouldShowRequestPermissionRationale +
                '}';
    }
}
