package com.x930073498.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by x930073498 on 2019/6/19.
 */
public abstract class DelegateBaseItem implements BaseItem {
    private HolderViewProvider delegate;

    public DelegateBaseItem(HolderViewProvider provider) {
        this.delegate = provider;
    }

    @Override
    public Object getData() {
        return this;
    }


    @Override
    public final View create(ViewGroup group, int viewType) {
        return delegate.create(group, viewType);
    }

    @Override
    public final int getType(Object data, int position) {
        return delegate.getType(data, position);
    }
}
