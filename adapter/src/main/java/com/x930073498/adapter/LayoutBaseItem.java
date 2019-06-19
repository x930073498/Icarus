package com.x930073498.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by x930073498 on 2019/6/19.
 */
public abstract class LayoutBaseItem implements BaseItem {
    @Override
    public  Object getData() {
        return this;
    }

    public abstract int getLayout(Object data, int position);

    @Override
    public final View create(ViewGroup group, int viewType) {
        return LayoutInflater.from(group.getContext()).inflate(viewType, group, false);
    }

    @Override
    public final int getType(Object data, int position) {
        return getLayout(data, position);
    }

}
