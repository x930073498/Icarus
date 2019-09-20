package com.x930073498.adapter;

public class InitialBundle<T> extends Bundle<T> {
    Source source;
    int position;
    CommonAdapter adapter;

    InitialBundle(BaseItem<T> item, T data) {
        super(item, data);
    }

    protected Source getSource() {
        return source;
    }

    public int getPosition() {
        return position;
    }

    public CommonAdapter getAdapter() {
        return adapter;
    }

    @Override
    public T getData() {
        return super.getData();
    }
}
