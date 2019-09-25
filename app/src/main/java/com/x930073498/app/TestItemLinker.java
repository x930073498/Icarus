package com.x930073498.app;

import android.util.Log;
import android.widget.TextView;

import com.x930073498.adapter.AbstractSelfItemLinker;
import com.x930073498.adapter.SourceBundle;
import com.x930073498.adapter.FactoryParams;
import com.x930073498.adapter.InitialBundle;
import com.x930073498.adapter.ViewHolder;

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
        Log.e(TAG, "onViewAttachedToWindow: " );
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
