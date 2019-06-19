package com.x930073498.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by x930073498 on 2019/6/19.
 */
public interface HolderViewProvider {
    View create(ViewGroup group, int viewType);

    int getType(Object data, int position);
}
