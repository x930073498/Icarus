package com.x930073498.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


/**
 * 数据源操作类
 */
@SuppressWarnings("ALL")
public final class Source {
    private List<SourceBundle> data = new ArrayList<>();
    private CommonAdapter adapter;
    FactoryPlugin plugin;
    private List<RecyclerCallback> callbacks = new ArrayList<>();

    void onViewRecycled(@NonNull ViewHolder holder) {
        SourceBundle bundle = holder.bundle;
        if (bundle != null) {
            BaseItem item = bundle.item;
            if (item != null) {
                item.onViewRecycled(bundle);
            }
        }
        for (RecyclerCallback callback : callbacks
        ) {
            callback.onViewRecycled(holder);
        }
    }

    void onFailedToRecycleView(@NonNull ViewHolder holder) {
        SourceBundle bundle = holder.bundle;
        if (bundle != null) {
            BaseItem item = bundle.item;
            if (item != null) {
                item.onFailedToRecycleView(bundle);
            }
        }
        for (RecyclerCallback callback : callbacks
        ) {
            callback.onFailedToRecycleView(holder);
        }
    }

    void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        SourceBundle bundle = holder.bundle;
        if (bundle != null) {
            BaseItem item = bundle.item;
            if (item != null) {
                item.onViewAttachedToWindow(bundle);
            }
        }
        for (RecyclerCallback callback : callbacks
        ) {
            callback.onViewAttachedToWindow(holder);
        }
    }

    void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        SourceBundle bundle = holder.bundle;
        if (bundle != null) {
            BaseItem item = bundle.item;
            if (item != null) {
                item.onViewDetachedFromWindow(bundle);
            }
        }
        for (RecyclerCallback callback : callbacks
        ) {
            callback.onViewDetachedFromWindow(holder);
        }
    }

    void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        for (RecyclerCallback callback : callbacks
        ) {
            callback.onAttachedToRecyclerView(recyclerView);
        }
    }

    void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        for (RecyclerCallback callback : callbacks
        ) {
            callback.onDetachedFromRecyclerView(recyclerView);
        }
    }


    public final void notifyDataSetChanged() {
        if (adapter == null) return;
        adapter.notifyDataSetChanged();
    }

    public final void notifyItemChanged(int position) {
        if (adapter == null) return;
        adapter.notifyItemChanged(position);
    }

    public final void notifyItemChanged(int position, @Nullable Object payload) {
        if (adapter == null) return;
        adapter.notifyItemChanged(position, payload);
    }

    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        if (adapter == null) return;
        adapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    public final void notifyItemRangeChanged(int positionStart, int itemCount,
                                             @Nullable Object payload) {
        if (adapter == null) return;
        adapter.notifyItemRangeChanged(positionStart, itemCount, payload);
    }

    public final void notifyItemInserted(int position) {
        if (adapter == null) return;
        adapter.notifyItemInserted(position);
    }

    public final void notifyItemMoved(int fromPosition, int toPosition) {
        if (adapter == null) return;
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    public final void notifyItemRangeInserted(int positionStart, int itemCount) {
        if (adapter == null) return;
        adapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    public final void notifyItemRemoved(int position) {
        if (adapter == null) return;
        adapter.notifyItemRemoved(position);
    }

    public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
        if (adapter == null) return;
        adapter.notifyItemRangeRemoved(positionStart, itemCount);
    }

    public CommonAdapter getAdapter() {
        return adapter;
    }

    public void removeCallback(RecyclerCallback callback) {
        callbacks.remove(callback);
    }

    public void addCallback(RecyclerCallback callback) {
        if (callbacks.contains(callback)) return;
        callbacks.add(callback);
    }

    private Source() {
        plugin = new FactoryPlugin(this);
    }

    int size() {
        return data == null ? 0 : data.size();
    }

    void setAdapter(CommonAdapter adapter) {
        this.adapter = adapter;
    }

    SourceBundle<?> getBundle(int position) {
        SourceBundle<?> bundle = data.get(position);
        bundle.source = this;
        return bundle;
    }

    void bind(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        SourceBundle bundle = getBundle(position);
        if (bundle == null) return;
        bundle.position = position;
        bundle.holder = holder;
        bundle.payloads = payloads;
        bundle.adapter = adapter;
        holder.bundle = bundle;
        BaseItem item = bundle.item;
        if (item != null) {
            item.bind(bundle);
        }
    }

    public Source insert(Bundle<?>... items) {
        return insert(0, items);
    }

    public Source insert(List<? extends Bundle> items) {
        return insert(0, items);
    }

    public Source insert(int index, Bundle<?>... items) {
        List<SourceBundle> list = new ArrayList<>();
        for (Bundle<?> item : items
        ) {
            if (item instanceof SourceBundle) {
                list.add((SourceBundle) item);
            }
        }
        data.addAll(index, list);
        return this;
    }

    public Source insert(int index, List<? extends Bundle> items) {
        if (items == null) return this;
        List<SourceBundle> list = new ArrayList<>();
        for (Bundle<?> item : items
        ) {
            if (item instanceof SourceBundle) {
                list.add((SourceBundle) item);
            }
        }
        data.addAll(index, list);
        return this;
    }


    public Source add(Bundle<?>... items) {
        insert(-1, items);
        return this;
    }

    public Source add(List<? extends Bundle> items) {
        if (items == null) return this;
        List<SourceBundle> list = new ArrayList<>();
        for (Bundle<?> item : items
        ) {
            if (item instanceof SourceBundle) {
                list.add((SourceBundle) item);
            }
        }
        data.addAll(list);
        return this;
    }

    public Source remove(Bundle<?>... data) {
        this.data.removeAll(Arrays.asList(data));
        return this;
    }

    public Source clear() {
        this.data.clear();
        return this;
    }

    public Source replace(Bundle<?>... data) {
        clear();
        add(data);
        return this;
    }

    public Source replace(List<? extends Bundle> items) {
        clear();
        add(items);
        return this;
    }


    public static Source create() {
        return new Source();
    }


    public void bind(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter instanceof CommonAdapter) {
            ((CommonAdapter) adapter).bind(recyclerView, this);
        } else {
            adapter = new CommonAdapter();
            ((CommonAdapter) adapter).bind(recyclerView, this);
        }
    }

}
