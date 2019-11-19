package com.x930073498.recycler;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自身提供创建 type并创建viewHolder
 *
 * @param <T>
 */
public abstract  class SimpleAbstractTypeItemLinker<T> extends AbstractSelfItemLinker<T>{

    @Override
    public ViewHolder create(FactoryParams params) {
        return params.create(params.viewType);
    }

    public abstract int getLayout(InitialBundle<T> bundle);
    @Override
    public final int type(InitialBundle<T> bundle) {
        return getLayout(bundle);
    }
}
