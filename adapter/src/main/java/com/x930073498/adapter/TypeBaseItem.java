package com.x930073498.adapter;

import androidx.annotation.NonNull;

public class TypeBaseItem<T> implements BaseItem<T> {
    private TypeProvider<T> provider;
    private BaseItem<T> item;

    TypeBaseItem(TypeProvider<T> provider, BaseItem<T> item) {
        this.item = item;
        this.provider = provider;
    }

    @Override
    public TypeProvider<T> getType(Bundle<T> bundle) {
        return provider;
    }

    @Override
    public HolderFactory createHolder(FactoryParams params) {
        return item.createHolder(params);
    }

    @Override
    public void bind(Bundle<T> bundle) {
        item.bind(bundle);
    }

    @Override
    public void onViewRecycled(@NonNull Bundle<T> bundle) {
        item.onViewRecycled(bundle);
    }

    @Override
    public void onFailedToRecycleView(@NonNull Bundle<T> bundle) {
        item.onFailedToRecycleView(bundle);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull Bundle<T> bundle) {
        item.onViewAttachedToWindow(bundle);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull Bundle<T> bundle) {
        item.onViewDetachedFromWindow(bundle);
    }
}
