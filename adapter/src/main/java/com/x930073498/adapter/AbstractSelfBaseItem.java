package com.x930073498.adapter;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自身提供创建 type并创建viewHolder
 *
 * @param <T>
 */
public abstract class AbstractSelfBaseItem<T> implements BaseItem<T>, TypeProvider<T>, HolderFactory {
    @Override
    public final HolderFactory createHolder(FactoryParams params) {
        return this;
    }


    @Override
    public final TypeProvider<T> getType(InitialBundle<T> bundle) {
        return this;
    }

    @Override
    public void onViewRecycled(@NonNull SourceBundle<T> bundle) {

    }

    @Override
    public void onFailedToRecycleView(@NonNull SourceBundle<T> bundle) {

    }

    @Override
    public void onViewAttachedToWindow(@NonNull SourceBundle<T> bundle) {

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SourceBundle<T> bundle) {

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {

    }
}
