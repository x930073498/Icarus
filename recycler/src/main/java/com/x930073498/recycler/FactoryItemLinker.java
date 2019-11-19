package com.x930073498.recycler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 用于代理ViewHolder初始化器，不开放
 *
 * @param <T>
 */
final class FactoryItemLinker<T> implements ItemLinker<T> {

    private ItemLinker<T> item;
    private HolderFactory factory;

    @Override
    public boolean equals(Object other) {
        return other instanceof FactoryItemLinker && ((FactoryItemLinker) other).getTypeId().equals(getTypeId());
    }

    @Override
    public int hashCode() {
        return getTypeId().hashCode();
    }

    FactoryItemLinker(ItemLinker<T> item, HolderFactory factory) {
        this.factory = factory;
        this.item = item;
    }

    @Override
    public final String getTypeId() {
        return item.getTypeId();
    }

    @Override
    public TypeProvider<T> getType(@NonNull InitialBundle<T> bundle) {
        return item.getType(bundle);
    }

    @Override
    public HolderFactory createHolder(@NonNull FactoryParams params) {
        return factory;
    }

    @Override
    public void bind(@NonNull SourceBundle<T> bundle) {
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
