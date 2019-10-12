package com.x930073498.recycler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 用于创建一个recycler 的item，主要作用：绑定数据
 *
 * @param <T>
 */
public interface ItemLinker<T> {

    /**
     * 解析type
     * @param bundle
     * @return
     */
    TypeProvider<T> getType(@NonNull InitialBundle<T> bundle);

    HolderFactory createHolder(@NonNull FactoryParams params);

    void bind(@NonNull SourceBundle<T> bundle);


    default void onViewRecycled(@NonNull SourceBundle<T> bundle) {
    }

    default void onFailedToRecycleView(@NonNull SourceBundle<T> bundle) {
    }

    default void onViewAttachedToWindow(@NonNull SourceBundle<T> bundle) {
    }

    default void onViewDetachedFromWindow(@NonNull SourceBundle<T> bundle) {
    }

    default void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

    }

    default void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {

    }


}
