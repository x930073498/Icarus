package com.x930073498.adapter;

/**
 * 用于解析type
 *
 * @param <T>
 */
public class InitialBundle<T> extends Bundle<T> {
    Source source;
    int position;
    StyleAdapter adapter;

    InitialBundle(ItemLinker<T> item, T data) {
        super(item, data);
    }

    protected Source getSource() {
        return source;
    }

    public int getPosition() {
        return position;
    }

    public StyleAdapter getAdapter() {
        return adapter;
    }

    @Override
    public T getData() {
        return super.getData();
    }
}
