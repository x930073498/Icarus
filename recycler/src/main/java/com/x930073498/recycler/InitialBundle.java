package com.x930073498.recycler;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 用于解析type
 *
 * @param <T>
 */
public class InitialBundle<T> extends Bundle<T> {
    Source source;
    int position;
    RecyclerView.Adapter adapter;

    InitialBundle(ItemLinker<T> item, T data) {
        super(item, data);
    }

    protected SourceInterface getSource() {
        return source;
    }

    public int getPosition() {
        return position;
    }

    public  RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public T getData() {
        return super.getData();
    }
}
