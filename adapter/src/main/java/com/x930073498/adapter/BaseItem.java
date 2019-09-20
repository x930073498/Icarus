package com.x930073498.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface BaseItem<T> {
    default TypeProvider<T> getType(Bundle<T> bundle) {
        int hash = getClass().hashCode();
        return it -> hash;
    }

    HolderFactory createHolder(FactoryParams params);

    void bind(Bundle<T> bundle);

    default void onViewRecycled(@NonNull Bundle<T> bundle) {
    }

    default void onFailedToRecycleView(@NonNull Bundle<T> bundle) {
    }

    default void onViewAttachedToWindow(@NonNull Bundle<T> bundle) {
    }

    default void onViewDetachedFromWindow(@NonNull Bundle<T> bundle) {
    }

   default void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

    }

    default void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {

    }


}
