package com.x930073498.recycler;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 内置ViewHolder
 */
@SuppressWarnings({"unchecked", "WeakerAccess"})
public final class ViewHolder extends RecyclerView.ViewHolder {
    SourceBundle<?> bundle;
    private SparseArray<View> views = new SparseArray<>();

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public <V extends View> V getView(int id) {
        View view = views.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            if (view != null) {
                views.put(id, view);
            }
        }
        try {
            return (V) view;
        } catch (Exception e) {
            return null;
        }
    }
}
