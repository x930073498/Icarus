package com.x930073498.recycler;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public interface SourceInterface< R extends SourceInterface<R>> {
    R notifyDataSetChanged();

    R notifyItemChanged(int position);

    R notifyItemChanged(int position, @Nullable Object payload);

    R notifyItemRangeChanged(int positionStart, int itemCount);

    R notifyItemRangeChanged(int positionStart, int itemCount,
                             @Nullable Object payload);

    R notifyItemInserted(int position);

    R notifyItemMoved(int fromPosition, int toPosition);

    R notifyItemRangeInserted(int positionStart, int itemCount);

    R notifyItemRemoved(int position);

    R notifyItemRangeRemoved(int positionStart, int itemCount);

    RecyclerView.Adapter getAdapter();

    void removeCallback(RecyclerCallback callback);

    R addCallback(RecyclerCallback callback);


    SourceBundle<?> getBundle(int position);


    SourceBundle<?> getBundle(Object source);

    int getBundlePosition(SourceBundle bundle);

    int getBundlePositionWithSource(Object source);


    R insert(Bundle<?>... items);

    R insert(List<? extends Bundle> items);

    R insert(int index, Bundle<?>... items);

    R move(int start, int to, int count);

    R move(int start, int to);

    R move(SourceBundle<?> from, SourceBundle<?> to);


    R moveSource(Object from, Object to);


    R swap(int from, int to, int count);

    R swap(int from, int to);

    R swap(SourceBundle<?> from, SourceBundle<?> to);

    R swapSource(Object from, Object to);


    R removeRange(int start, int count);

    R moveSource(int to, Object data);

    R insert(int index, List<? extends Bundle> items);


    R add(Bundle<?>... items);

    R add(List<? extends Bundle> items);

    R remove(Bundle<?>... data);

    R remove(List<Bundle> data);

    R removeSource(Object... data);

    R removeSource(List<?> data);

    R clear();

    R removeAt(int... indexes);

    R removeAt(List<Integer> indexes);

    R replace(Bundle<?>... data);

    R replace(List<? extends Bundle> items);
}
