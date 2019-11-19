package com.x930073498.recycler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 代理viewType的解析，提供内置BaseItem,不开放
 *
 * @param <T>
 */
final class TypeItemLinker<T> implements ItemLinker<T> {
    private TypeProvider<T> provider;
    private ItemLinker<T> item;


    @Override
    public boolean equals(Object other) {
        return other instanceof  TypeItemLinker &&((TypeItemLinker) other).getTypeId().equals(getTypeId());
    }
    @Override
    public int hashCode(){
        return getTypeId().hashCode();
    }

    TypeItemLinker(TypeProvider<T> provider, ItemLinker<T> item) {
        this.item = item;
        this.provider = provider;
    }

    @Override
    public final String getTypeId() {
        return item.getTypeId();
    }

    @Override
    public TypeProvider<T> getType(@NonNull InitialBundle<T> bundle) {
        return provider;
    }

    @Override
    public HolderFactory createHolder(@NonNull FactoryParams params) {
        return item.createHolder(params);
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
