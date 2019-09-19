package com.x930073498.app;

import android.util.Log;
import android.widget.TextView;

import com.x930073498.adapter.AbstractBaseItem;
import com.x930073498.adapter.Bundle;
import com.x930073498.adapter.FactoryParams;
import com.x930073498.adapter.ViewHolder;

import androidx.annotation.NonNull;

public class TestBaseItem extends AbstractBaseItem<String> {

    @Override
    public void bind(Bundle<String> bundle) {
        TextView tv = bundle.getView(R.id.tv);
        if (tv != null) {
            tv.setText(bundle.getData());
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull Bundle<String> bundle) {

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull Bundle<String> bundle) {
    }

    @Override
    public void onViewRecycled(@NonNull Bundle<String> bundle) {

    }

    @Override
    public ViewHolder create(FactoryParams params) {
        Log.e("tag", "enter this line ");
        return params.create(params.getViewType());
    }

    @Override
    public int type(Bundle<String> bundle) {
        return R.layout.layout_item_test;
    }
}
