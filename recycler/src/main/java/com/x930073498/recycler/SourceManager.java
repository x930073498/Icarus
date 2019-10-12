package com.x930073498.recycler;

import android.content.Context;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SourceManager {
    private VirtualLayoutManager manager;
    private CommonAdapter adapter;

    private SourceManager(Context context) {
        manager = new VirtualLayoutManager(context);
        adapter = new CommonAdapter(manager, false);
    }


    public SourceManager bind(RecyclerView recycler) {
        if (recycler != null) {
            recycler.setLayoutManager(manager);
            recycler.setAdapter(adapter);
        }
        return this;
    }

    public SourceManager addSource(Source... sources) {
        List<DelegateAdapter.Adapter> adapters = new ArrayList<>();
        for (Source source : sources
        ) {
            if (source == null) continue;
            source.delegateAdapter = adapter;
            adapters.add(source.adapter);
            source.isBound = true;
        }
        adapter.addAdapters(adapters);
        return this;
    }

    public SourceManager addSources(List<Source> sources) {
        List<DelegateAdapter.Adapter> adapters = new ArrayList<>();
        for (Source source : sources
        ) {
            if (source == null) continue;
            source.delegateAdapter = adapter;
            source.isBound = true;
            adapters.add(source.adapter);
        }
        adapter.addAdapters(adapters);
        return this;
    }

    public SourceManager removeSources(List<Source> sources) {
        List<DelegateAdapter.Adapter> adapters = new ArrayList<>();
        for (Source source : sources
        ) {
            if (source == null) continue;
            adapters.add(source.adapter);
            source.isBound = false;
        }
        adapter.removeAdapters(adapters);
        return this;
    }

    public SourceManager removeSource(Source... sources) {
        List<DelegateAdapter.Adapter> adapters = new ArrayList<>();
        for (Source source : sources
        ) {
            if (source == null) continue;
            adapters.add(source.adapter);
            source.isBound = false;
        }
        adapter.removeAdapters(adapters);
        return this;
    }


    public static SourceManager create(Context context) {
        return new SourceManager(context);
    }


}
