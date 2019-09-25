package com.x930073498.recycler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 用于回调
 */
public interface RecyclerCallback {
    default void onViewRecycled(@NonNull ViewHolder holder) {

    }

    default void onFailedToRecycleView(@NonNull ViewHolder holder) {
    }

    default void onViewAttachedToWindow(@NonNull ViewHolder holder) {

    }

    default void onViewDetachedFromWindow(@NonNull ViewHolder holder) {

    }

    default void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    }

    default void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    }
}
