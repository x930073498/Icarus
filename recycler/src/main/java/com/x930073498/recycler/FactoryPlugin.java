package com.x930073498.recycler;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;

/**
 * 内部实现viewType与viewHolder的关联
 */
final class FactoryPlugin {
    private Source source;

    private ViewType viewType = new ViewType();

    FactoryPlugin(Source source) {
        this.source = source;
    }

    private class ViewType {
        SparseArray<FactoryHolder> holders = new SparseArray<>();
        private AtomicInteger ai = new AtomicInteger();
        /**
         * key linker返回的type
         * value 返回delegateAdapter的type
         */
        private SparseIntArray types = new SparseIntArray();


        private FactoryHolder getFactoryHolder(int type) {
            return holders.get(type);
        }

        private void clear() {
            holders.clear();
        }

        private int getViewType(int type, SourceBundle<?> bundle) {
            int result = types.get(type, -1);
            if (result == -1) {
                result = ai.incrementAndGet();
                types.put(type, result);
            }
            FactoryHolder holder = holders.get(result);
            if (holder == null) {
                FactoryParams params = new FactoryParams(source, type);
                holder = new FactoryHolder(params, bundle.getHolderFactory(params), type);
                holders.put(result, holder);
            }
            return result;
        }
    }


    int getItemViewType(int position) {
        SourceBundle bundle = source.getBundle(source.getIndex(position));
        bundle.position = position;
        int type = bundle.getViewType();

        return viewType.getViewType(type, bundle);
    }

    private class FactoryHolder {
        private FactoryHolder(FactoryParams params, HolderFactory factory, int type) {
            this.factory = factory;
            this.params = params;
            this.type = type;
        }

        int type;
        FactoryParams params;
        HolderFactory factory;
    }

    ViewHolder create(@NonNull ViewGroup parent, int type) {
        FactoryHolder holder = viewType.getFactoryHolder(type);
        if (holder == null) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_empty, parent));
        }
        FactoryParams params = holder.params;
        params.parent = parent;
        params.viewType = holder.type;
        return holder.factory.create(params);
    }


}
