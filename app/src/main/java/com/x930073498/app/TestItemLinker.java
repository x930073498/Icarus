package com.x930073498.app;

import android.util.Log;
import android.widget.TextView;

import com.x930073498.recycler.AbstractSelfItemLinker;
import com.x930073498.recycler.SourceBundle;
import com.x930073498.recycler.FactoryParams;
import com.x930073498.recycler.InitialBundle;
import com.x930073498.recycler.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestItemLinker extends AbstractSelfItemLinker<String> {

    private static final String TAG = "tag";

    @Override
    public void bind(SourceBundle<String> bundle) {
        TextView tv = bundle.getView(R.id.tv);
        if (tv != null) {
            tv.setText(bundle.getData());
            tv.setBackgroundColor(0xff876384);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SourceBundle<String> bundle) {
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SourceBundle<String> bundle) {
    }

    @Override
    public void onViewRecycled(@NonNull SourceBundle<String> bundle) {

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.e(TAG, "onAttachedToRecyclerView: " );

    }

    @Override
    public ViewHolder create(FactoryParams params) {
        return params.create(params.getViewType());
    }

    @Override
    public int type(InitialBundle<String> bundle) {
        return R.layout.layout_item_test;
    }
}
