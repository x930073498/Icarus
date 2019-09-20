package com.x930073498.adapter;

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
        SparseArray<SparseIntArray> types = new SparseArray<>();
        SparseArray<FactoryHolder> holders = new SparseArray<>();
        int result;
        private AtomicInteger ai = new AtomicInteger();

        private FactoryHolder getFactoryHolder(int type) {
            return holders.get(type);
        }

        private void clear() {
            types.clear();
            holders.clear();
        }

        private int getViewType(int type, int hash, SourceBundle<?> bundle) {
            if (type == 0) return 0;
            SparseIntArray typeAndResult = types.get(hash);
            if (typeAndResult == null) {
                result = ai.incrementAndGet();
                typeAndResult = new SparseIntArray();
                typeAndResult.put(type, result);
                types.put(hash, typeAndResult);
            } else {
                result = typeAndResult.get(type, 0);
                if (result == 0) {
                    result = ai.incrementAndGet();
                    typeAndResult.put(type, result);
                }
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
        SourceBundle bundle = source.getBundle(position);
        bundle.position = position;
        int type = bundle.getViewType();
        int hash = bundle.getTypeHash();
        return viewType.getViewType(type, hash, bundle);
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
