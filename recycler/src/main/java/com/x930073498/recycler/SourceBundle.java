package com.x930073498.recycler;

import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * 每个item绑定的数据
 *
 * @param <T>
 */
public final class SourceBundle<T> extends InitialBundle<T> {

    ViewHolder holder;
    List<Object> payloads = new ArrayList<>();
    OutBundle out = new OutBundle();

    SourceBundle(ItemLinker<T> item, T data) {
        super(item, data);
    }

    public int getViewType() {
        if (item == null) return 0;
        TypeProvider<T> provider = item.getType(this);
        if (provider != null) {
            return provider.type(this);
        }
        return 0;
    }

    @Override
    public SourceInterface getSource() {
        return super.getSource();
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


    public List<Object> getPayloads() {
        return payloads;
    }


    public void setOut(Object out) {
        this.out.out = out;
        this.out.data = getData();
        this.out.index = getPosition();
        source.notifyOut(this.out);
    }

    public <V extends View> V getView(int id) {
        return holder.getView(id);
    }
}
