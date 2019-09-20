package com.x930073498.app;

import android.widget.TextView;

import com.x930073498.adapter.AbstractSelfBaseItem;
import com.x930073498.adapter.FactoryParams;
import com.x930073498.adapter.InitialBundle;
import com.x930073498.adapter.SourceBundle;
import com.x930073498.adapter.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestItem extends AbstractSelfBaseItem<String> {
    private static final String TAG = "tag";

    @Override
    public void bind(SourceBundle<String> bundle) {
        TextView tv = bundle.getView(R.id.tvTitle);
        tv = tv == null ? bundle.getView(R.id.tv) : tv;
        if (tv != null) {
            tv.setText(bundle.getData());
        }
    }

    @Override
    public ViewHolder create(FactoryParams params) {
        return params.create(params.getViewType());
    }

    @Override
    public int type(InitialBundle<String> bundle) {
        if (bundle.getPosition() % 2 == 0) {
            return R.layout.layout_item_title;
        } else {
            return R.layout.layout_item_test;
        }
    }

    @Override
    public void onViewRecycled(@NonNull SourceBundle<String> bundle) {
        super.onViewRecycled(bundle);
    }

    @Override
    public void onFailedToRecycleView(@NonNull SourceBundle<String> bundle) {
        super.onFailedToRecycleView(bundle);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SourceBundle<String> bundle) {
        super.onViewAttachedToWindow(bundle);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SourceBundle<String> bundle) {
        super.onViewDetachedFromWindow(bundle);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
