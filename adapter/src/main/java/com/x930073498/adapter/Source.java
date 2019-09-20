package com.x930073498.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


/**
 * 数据源操作类
 */
@SuppressWarnings("ALL")
public final class Source {
    private List<SourceBundle> data = new ArrayList<>();
    private HashSet<BaseItem> items = new HashSet<>();
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
        for (BaseItem item : getItems()
        ) {
            if (item != null) {
                item.onAttachedToRecyclerView(recyclerView);
            }
        }
        for (RecyclerCallback callback : callbacks
        ) {
            callback.onAttachedToRecyclerView(recyclerView);
        }
    }

    void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        for (BaseItem item : getItems()
        ) {
            if (item != null) {
                item.onDetachedFromRecyclerView(recyclerView);
            }
        }
        for (RecyclerCallback callback : callbacks) {
            callback.onDetachedFromRecyclerView(recyclerView);
        }
    }


    public final Source notifyDataSetChanged() {
        if (adapter == null) return this;
        adapter.notifyDataSetChanged();
        return this;
    }

    private Set<BaseItem> getItems() {
        Set<BaseItem> result = new HashSet<>();
        for (SourceBundle bundle : data
        ) {
            result.add(bundle.item);
        }
        return result;
    }

    public final Source notifyItemChanged(int position) {
        if (adapter == null) return this;
        adapter.notifyItemChanged(position);
        return this;
    }

    public final Source notifyItemChanged(int position, @Nullable Object payload) {
        if (adapter == null) return this;
        adapter.notifyItemChanged(position, payload);
        return this;
    }

    public final Source notifyItemRangeChanged(int positionStart, int itemCount) {
        if (adapter == null) return this;
        adapter.notifyItemRangeChanged(positionStart, itemCount);
        return this;
    }

    public final Source notifyItemRangeChanged(int positionStart, int itemCount,
                                               @Nullable Object payload) {
        if (adapter == null) return this;
        adapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        return this;
    }

    public final Source notifyItemInserted(int position) {
        if (adapter == null) return this;
        adapter.notifyItemInserted(position);
        return this;
    }

    public final Source notifyItemMoved(int fromPosition, int toPosition) {
        if (adapter == null) return this;
        adapter.notifyItemMoved(fromPosition, toPosition);
        return this;
    }

    public final Source notifyItemRangeInserted(int positionStart, int itemCount) {
        if (adapter == null) return this;
        adapter.notifyItemRangeInserted(positionStart, itemCount);
        return this;
    }

    public final Source notifyItemRemoved(int position) {
        if (adapter == null) return this;
        adapter.notifyItemRemoved(position);
        return this;
    }

    public final Source notifyItemRangeRemoved(int positionStart, int itemCount) {
        if (adapter == null) return this;
        adapter.notifyItemRangeRemoved(positionStart, itemCount);
        return this;
    }

    public CommonAdapter getAdapter() {
        return adapter;
    }

    public void removeCallback(RecyclerCallback callback) {
        callbacks.remove(callback);
    }

    public Source addCallback(RecyclerCallback callback) {
        if (callbacks.contains(callback)) return this;
        callbacks.add(callback);
        return this;
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

    public SourceBundle<?> getBundle(int position) {
        SourceBundle<?> bundle = data.get(position);
        bundle.source = this;
        return bundle;
    }

    public SourceBundle<?> getBundle(Object source) {
        for (SourceBundle bundle : data
        ) {
            if (equals(source, bundle.getData())) return bundle;
        }
        return null;
    }

    public int getBundlePosition(SourceBundle bundle) {
        return data.indexOf(bundle);
    }

    public int getBundlePositionWithSource(Object source) {
        SourceBundle bundle;
        for (int i = 0; i < data.size(); i++) {
            bundle = data.get(i);
            if (bundle == null) continue;
            if (equals(source, bundle.getData())) return i;

        }
        return -1;
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

    public Source move(int start, int to, int count) {
        if (start == to || start < 0 || start + count >= data.size() || to < 0 || to + count >= data.size())
            return this;
        List<SourceBundle> list = new ArrayList<>(data.subList(start, start + count));
        removeRange(start, count);
        return insert(to, list);
    }

    public Source move(int start, int to) {
        return move(start, to, 1);
    }

    public Source move(SourceBundle<?> from, SourceBundle<?> to) {
        if (from == null || to == null) return this;
        int fromIndex = data.indexOf(from);
        int toIndex = data.indexOf(to);
        if (fromIndex < 0 || toIndex < 0) return this;
        return move(fromIndex, toIndex);
    }

    boolean equals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public Source moveSource(Object from, Object to) {
        if (from == null || to == null) return this;
        if (equals(from, to)) return this;
        int fromIndex = -1, toIndex = -1;
        SourceBundle bundle;
        for (int i = 0; i < data.size(); i++) {
            bundle = getBundle(i);
            if (bundle == null) continue;
            if (equals(from, bundle.getData())) {
                fromIndex = i;
            } else if (equals(to, bundle.getData())) {
                toIndex = i;
            }
            if (fromIndex != -1 && toIndex != -1) break;
        }
        if (fromIndex == -1 || toIndex == -1) return this;
        return move(fromIndex, toIndex);
    }


    public Source swap(int from, int to, int count) {
        if (data.isEmpty()) return this;
        if (from < 0 || to < 0) return this;
        if (from + count > data.size() || to + count > data.size()) return this;
        if (from > to && to + count > from) return this;
        if (from < to && from + count > to) return this;
        SourceBundle first, second;
        int firstIndex, secondIndex;
        for (int i = 0; i < count; i++) {
            firstIndex = i + from;
            secondIndex = i + to;
            first = getBundle(firstIndex);
            second = getBundle(secondIndex);
            data.set(firstIndex, second);
            data.set(secondIndex, first);
        }
        return this;
    }

    public Source swap(int from, int to) {
        return swap(from, to, 1);
    }

    public Source swap(SourceBundle<?> from, SourceBundle<?> to) {
        if (data.isEmpty()) return this;
        int first = data.indexOf(from);
        int second = data.indexOf(to);
        if (first < 0 || second < 0) return this;
        data.set(second, from);
        data.set(first, to);
        return this;
    }

    public Source swapSource(Object from, Object to) {
        if (data.isEmpty()) return this;
        if (from == null || to == null) return this;
        int first = -1, second = -1;
        SourceBundle fromBundle = null, toBundle = null, tempBundle;
        for (int i = 0; i < data.size(); i++) {
            tempBundle = data.get(i);
            if (tempBundle == null) continue;
            if (equals(from, tempBundle.getData())) {
                fromBundle = tempBundle;
                first = i;
            } else if (equals(to, tempBundle.getData())) {
                toBundle = tempBundle;
                second = i;
            }
            if (first != -1 && second != -1) break;
        }
        if (first == -1 || second == -1) return this;
        data.set(first, toBundle);
        data.set(second, fromBundle);
        return this;
    }


    public Source removeRange(int start, int count) {
        if (start < 0 || start >= data.size() || count <= 0) return this;
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            indexes.add(start + i);
        }
        return removeAt(indexes);
    }

    public Source moveSource(int to, Object data) {
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

    public Source remove(List<Bundle> data) {
        this.data.removeAll(data);
        return this;
    }

    public Source removeSource(Object... data) {
        return removeSource(Arrays.asList(data));
    }

    public Source removeSource(List<?> data) {
        if (data.isEmpty()) return this;
        Iterator<SourceBundle> iter = this.data.iterator();
        Bundle bundle;
        while (iter.hasNext()) {
            bundle = iter.next();
            if (bundle != null && data.contains(bundle.getData())) {
                iter.remove();
            }
        }
        return this;
    }

    public Source clear() {
        this.data.clear();
        return this;
    }

    public Source removeAt(int... indexes) {
        List<Bundle> list = new ArrayList<>();
        Bundle bundle;
        for (int index : indexes
        ) {
            if (index < 0 || index >= data.size()) continue;
            list.add(data.get(index));
        }
        remove(list);
        return this;
    }

    public Source removeAt(List<Integer> indexes) {
        List<Bundle> list = new ArrayList<>();
        Bundle bundle;
        for (int index : indexes
        ) {
            if (index < 0 || index >= data.size()) continue;
            list.add(data.get(index));
        }
        remove(list);
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


    public Source bind(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter instanceof CommonAdapter) {
            ((CommonAdapter) adapter).bind(recyclerView, this);
        } else {
            adapter = new CommonAdapter();
            ((CommonAdapter) adapter).bind(recyclerView, this);
        }
        return this;
    }

}
