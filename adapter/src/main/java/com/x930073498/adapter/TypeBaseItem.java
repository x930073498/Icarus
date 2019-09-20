package com.x930073498.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 代理viewType的解析，提供内置BaseItem,不开放
 *
 * @param <T>
 */
final class TypeBaseItem<T> implements BaseItem<T> {
    private TypeProvider<T> provider;
    private BaseItem<T> item;

    TypeBaseItem(TypeProvider<T> provider, BaseItem<T> item) {
        this.item = item;
        this.provider = provider;
    }

    @Override
    public TypeProvider<T> getType(InitialBundle<T> bundle) {
        return provider;
    }

    @Override
    public HolderFactory createHolder(FactoryParams params) {
        return item.createHolder(params);
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
