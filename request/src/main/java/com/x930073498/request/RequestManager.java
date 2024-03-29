package com.x930073498.request;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Process;

import com.x930073498.context.ContextManager;
import com.x930073498.context.State;
import com.x930073498.context.StateListener;
import com.x930073498.request.event.DefaultIslandProvider;
import com.x930073498.request.core.Event;
import com.x930073498.request.core.IslandProvider;
import com.x930073498.request.permission.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by x930073498 on 2019/6/20.
 * 请求权限回调和startActivityForResult回调
 */
public class RequestManager {

    public static final String TAG = System.currentTimeMillis() + "IslandLoader";
    private static final Map<Activity, Event> map = Collections.synchronizedMap(new HashMap<Activity, Event>());
    private static List<IslandProvider> providers = Collections.synchronizedList(new ArrayList<IslandProvider>());

    static {
        register(new DefaultIslandProvider());
        ContextManager.registerStateListener(new StateListener() {
            @Override
            public void onState(Activity activity, State state) {
                if (state == State.DESTROYED) {
                    map.remove(activity);
                }
            }
        });
    }

    public synchronized static void register(IslandProvider provider) {
        providers.add(0, provider);
    }

    private static Event get(Activity activity) {
        if (activity == null) throw new RuntimeException("activity not found");
        Event event = map.get(activity);
        if (event != null) return event;
        for (IslandProvider provider : providers
        ) {
            event = provider.provide(activity, TAG);
            if (event != null) {
                break;
            }
        }

        if (event == null) {
            throw new RuntimeException("has No Event created");
        }
        map.put(activity, event);
        return event;
    }


    public synchronized static Event attach(Activity activity) {
        return RequestManager.get(activity);
    }

    public static boolean isValid(String permission) {
        return Permission.isValid(permission);
    }

    public static boolean isGranted(String permission) {

        return !isValid(permission) || ContextManager.getApplicationContext().checkPermission(permission, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    public static Event guess() {
        return attach(ContextManager.getTopActivity());
    }
}
