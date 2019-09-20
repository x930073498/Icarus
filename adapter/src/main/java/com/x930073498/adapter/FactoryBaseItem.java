package com.x930073498.adapter;

import androidx.annotation.NonNull;

public class FactoryBaseItem<T> implements BaseItem<T> {

    private BaseItem<T> item;
    private HolderFactory factory;

    FactoryBaseItem(BaseItem<T> item, HolderFactory factory) {
        this.factory = factory;
        this.item = item;
    }

    @Override
    public TypeProvider<T> getType(Bundle<T> bundle) {
        return item.getType(bundle);
    }

    @Override
    public HolderFactory createHolder(FactoryParams params) {
        return factory;
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
