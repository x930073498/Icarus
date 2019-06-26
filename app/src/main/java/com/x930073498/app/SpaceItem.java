package com.x930073498.app;

import androidx.annotation.NonNull;

import com.x930073498.adapter.BaseItem;
import com.x930073498.adapter.CommonAdapter;
import com.x930073498.adapter.CommonHolder;
import com.x930073498.adapter.LayoutBaseItem;

import java.util.List;

/**
 * Created by x930073498 on 2019/6/24.
 */
public class SpaceItem extends LayoutBaseItem {
    @Override
    public int getLayout(Object data, int position) {
        return R.layout.layout_item_space;
    }

    @Override
    public void bind(CommonAdapter adapter, CommonHolder holder, int position, Object data, List<BaseItem> source, @NonNull List<Object> payloads) {

    }
}
