package com.x930073498.recycler;

import android.util.Log;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommonAdapter extends DelegateAdapter {
    public CommonAdapter(VirtualLayoutManager layoutManager) {
        super(layoutManager);
    }

    public CommonAdapter(VirtualLayoutManager layoutManager, boolean hasConsistItemType) {
        super(layoutManager, hasConsistItemType);
    }


    @Override
    public int getAdaptersCount() {
        return super.getAdaptersCount();
    }

}
