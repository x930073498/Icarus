package com.x930073498.adapter;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public final class CommonAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Source source;

    CommonAdapter() {

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        source.onViewRecycled(holder);
        super.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull ViewHolder holder) {
        source.onFailedToRecycleView(holder);
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        source.onViewAttachedToWindow(holder);
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        source.onViewDetachedFromWindow(holder);
        super.onViewDetachedFromWindow(holder);
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        source.onAttachedToRecyclerView(recyclerView);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        source.onDetachedFromRecyclerView(recyclerView);
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return source.plugin.create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        source.bind(holder, position, new ArrayList<>());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        source.bind(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return source.size();
    }

    @Override
    public int getItemViewType(int position) {
        return source.plugin.getItemViewType(position);
    }

    void bind(RecyclerView recyclerView, Source source) {
        this.source = source;
        source.setAdapter(this);
        recyclerView.setAdapter(this);
    }
}
