package com.x930073498.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class CommonAdapter extends RecyclerView.Adapter<CommonHolder> {
    private List<BaseItem> source = new ArrayList<>();
    private SparseArray<BaseItem> types = new SparseArray<>();


    public synchronized void insert(int index, BaseItem... items) {
        index = Math.max(0, Math.min(source.size(), index));
        source.addAll(index, Arrays.asList(items));
    }

    public synchronized void insert(BaseItem... items) {
        insert(0, Arrays.asList(items));
    }

    public synchronized void replace(Collection<BaseItem> items) {
        source.clear();
        if (items == null) return;
        source.addAll(items);
    }

    public synchronized void clear() {
        source.clear();
    }

    public synchronized void insert(int index, Collection<BaseItem> items) {
        index = Math.max(0, Math.min(source.size(), index));
        if (items == null) return;
        source.addAll(index, items);
    }

    public synchronized void append(BaseItem... items) {
        source.addAll(Arrays.asList(items));
    }

    public synchronized void append(Collection<BaseItem> items) {
        if (items == null) return;
        source.addAll(items);
    }

    public synchronized void remove(BaseItem... items) {
        remove(Arrays.asList(items));
    }

    public synchronized void remove(Collection<BaseItem> items) {
        if (items == null) return;
        for (BaseItem item : items
        ) {
            source.remove(item);
        }
    }


    public synchronized void remove(int... indexes) {
        List<BaseItem> items = new ArrayList<>();
        BaseItem item;
        for (int index : indexes
        ) {
            if (index >= 0 && index < source.size()) {
                item = source.get(index);
                if (item != null) {
                    items.add(item);
                }
            }
        }
        remove(items);
    }

    public synchronized void move(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) return;
        BaseItem item = getItem(fromIndex);
        move(fromIndex, toIndex, Collections.singletonList(item));
    }

    public synchronized void move(BaseItem item, int toIndex) {
        int fromIndex = source.indexOf(item);
        move(fromIndex, toIndex, Collections.singletonList(item));
    }

    public synchronized void move(int fromIndex, int toIndex, int count) {
        if (fromIndex + count < source.size()) return;
        List<BaseItem> items = source.subList(fromIndex, fromIndex + count);
        move(fromIndex, toIndex, items);
    }


    public synchronized void exchange(int index1, int index2) {
        BaseItem item1 = getItem(index1);
        BaseItem item2 = getItem(index2);
        if (item1 == null || item2 == null) return;
        source.set(index1, item2);
        source.set(index2, item1);
    }

    public synchronized void exchange(BaseItem item, int index) {
        int index1 = source.indexOf(item);
        BaseItem item1 = getItem(index);
        if (index1 < 0 || item1 == null) return;
        source.set(index1, item1);
        source.set(index, item);
    }


    private void move(int fromIndex, int toIndex, Collection<BaseItem> items) {
        if (items == null || items.size() == 0) return;
        if (fromIndex < 0 || toIndex < 0) return;
        if (fromIndex >= source.size() || toIndex >= source.size()) return;
        if (fromIndex == toIndex) return;
        remove(items);
        int size = items.size();
        if (fromIndex > toIndex) {
            insert(toIndex, items);
        } else {
            insert(toIndex - size, items);
        }
    }


    public BaseItem getItem(int index) {
        if (index < 0 || index >= source.size()) return null;
        return source.get(index);
    }

    public Object getData(int index) {
        BaseItem item = getItem(index);
        if (item == null) return null;
        return item.getData();
    }

    public List<BaseItem> getSource() {
        return new ArrayList<>(source);
    }

    @NonNull
    @Override
    public CommonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseItem item = types.get(viewType);
        View view;
        if (item == null)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_empty, parent, false);
        else view = item.create(parent, viewType);
        return new CommonHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull CommonHolder holder, int position, @NonNull List<Object> payloads) {
        BaseItem item = source.get(position);
        if (item == null) return;
        item.bind(this, holder, position, item.getData(), source, payloads);
    }

    @Override
    public int getItemCount() {
        return source.size();
    }

    @Override
    public int getItemViewType(int position) {
        BaseItem item = source.get(position);
        int type = item.getType(item.getData(), position);
        types.put(type, item);
        return type;
    }
}
