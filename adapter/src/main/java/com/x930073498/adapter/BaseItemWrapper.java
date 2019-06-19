package com.x930073498.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class BaseItemWrapper implements BaseItem {

    private BaseItem item;
    private Object data;

    private BaseItemWrapper(BaseItem item, Object data) {
        this.item = item;
        this.data = data;
    }

    @Override
    public final Object getData() {
        return data;
    }

    @Override
    public final void bind(CommonAdapter adapter, CommonHolder holder, int position, Object data, List<BaseItem> source, @NonNull List<Object> payloads) {
        item.bind(adapter, holder, position, this.data, source, payloads);
    }

    @Override
    public final View create(ViewGroup group, int viewType) {
        return item.create(group, viewType);
    }

    @Override
    public final int getType(Object data, int position) {
        return item.getType(this.data, position);
    }

    public BaseItem getItem() {
        return item;
    }


    public static BaseItem wrap(BaseItem item, Object data) {
        return new BaseItemWrapper(item, data);
    }

    public static List<BaseItem> wrap(BaseItem item, List<?> data) {
        List<BaseItem> result = new ArrayList<>();
        if (data == null) return result;
        for (Object temp : data
        ) {
            result.add(wrap(item, temp));
        }
        return result;
    }
}
