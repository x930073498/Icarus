package com.x930073498.adapter;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public final class Bundle<T> extends SourceItem<T> {

    Source source;
    int position;
    ViewHolder holder;
    CommonAdapter adapter;
    List<Object> payloads = new ArrayList<>();


    Bundle(BaseItem<T> item, T data) {
        super(item, data);
    }

    public Source getSource() {
        return source;
    }

    @Override
    public T getData() {
        return super.getData();
    }

    public int getPosition() {
        return position;
    }

    public int getViewType() {
        if (item == null) return 0;
        TypeProvider<T> provider = item.getType(this);
        if (provider != null) {
            return provider.type(this);
        }
        return 0;
    }

    int getTypeHash() {
        return item.getClass().hashCode();
    }

    int getHash() {
        return item.hashCode();
    }

    public HolderFactory getHolderFactory(FactoryParams params) {
        return item.createHolder(params);
    }

    public ViewHolder getHolder() {
        return holder;
    }

    public CommonAdapter getAdapter() {
        return adapter;
    }

    public List<Object> getPayloads() {
        return payloads;
    }


    public <V extends View> V getView(int id) {
        return holder.getView(id);
    }
}
