package com.x930073498.app;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.x930073498.adapter.BaseItem;
import com.x930073498.adapter.CommonAdapter;
import com.x930073498.adapter.CommonHolder;
import com.x930073498.adapter.LayoutBaseItem;

import java.util.List;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class TestItem extends LayoutBaseItem {
    @Override
    public int getLayout(Object data, int position) {
        return R.layout.layout_item_test;
    }


    @Override
    public void bind(CommonAdapter adapter, CommonHolder holder, int position, Object data, List<BaseItem> source, @NonNull List<Object> payloads) {
        if (data instanceof TestData) {
            TextView tv = holder.getView(R.id.tv);
            if (tv != null) {
                tv.setText(((TestData) data).getName());
            }
        }
    }
}
