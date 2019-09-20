package com.x930073498.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 用于代理ViewHolder初始化器，不开放
 *
 * @param <T>
 */
final class FactoryBaseItem<T> implements BaseItem<T> {

    private BaseItem<T> item;
    private HolderFactory factory;

    FactoryBaseItem(BaseItem<T> item, HolderFactory factory) {
        this.factory = factory;
        this.item = item;
    }

    @Override
    public TypeProvider<T> getType(InitialBundle<T> bundle) {
        return item.getType(bundle);
    }

    @Override
    public HolderFactory createHolder(FactoryParams params) {
        return factory;
    }

    @Override
    public void bind(SourceBundle<T> bundle) {
        item.bind(bundle);
    }

    @Override
    public void onViewRecycled(@NonNull SourceBundle<T> bundle) {
        item.onViewRecycled(bundle);
    }

    @Override
    public void onFailedToRecycleView(@NonNull SourceBundle<T> bundle) {
        item.onFailedToRecycleView(bundle);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SourceBundle<T> bundle) {
        item.onViewAttachedToWindow(bundle);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SourceBundle<T> bundle) {
        item.onViewDetachedFromWindow(bundle);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        item.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        item.onDetachedFromRecyclerView(recyclerView);
    }
}
