package com.x930073498.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * 用于创建viewHolder，对外开放
 */
public final class FactoryParams {

    private Source source;
    ViewGroup parent;
    int viewType;

    FactoryParams(Source source, int viewType) {
        this.source = source;
        this.viewType = viewType;
    }

    public Source getSource() {
        return source;
    }

    public ViewGroup getParent() {
        return parent;
    }

    public Context getContext() {
        return parent.getContext();
    }
    public ViewHolder create(int layoutId){
        return new ViewHolder(LayoutInflater.from(getContext()).inflate(layoutId,parent,false));
    }

    public int getViewType() {
        return viewType;
    }
}
