package com.x930073498.recycler;

import android.view.View;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

class SimpleViewItemLinker implements ItemLinker<View> {
    private WeakReference<View> viewRef;

    SimpleViewItemLinker(View view) {
        this.viewRef = new WeakReference<>(view);
    }

    @Override
    public final String getTypeId() {
        return System.currentTimeMillis() + getClass().getName() + System.nanoTime();
    }

    @Override
    public final TypeProvider<View> getType(@NonNull InitialBundle<View> bundle) {
        return bundle1 -> 0;
    }

    @Override
    public final HolderFactory createHolder(@NonNull FactoryParams params) {
        View view = viewRef.get();
        if (view != null)
            return params1 -> new ViewHolder(view);
        else return params1 -> params.create(R.layout.layout_item_empty);
    }

    @Override
    public final void bind(@NonNull SourceBundle<View> bundle) {

    }
}
