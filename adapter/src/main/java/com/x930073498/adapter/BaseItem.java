package com.x930073498.adapter;

import androidx.annotation.NonNull;


import java.util.List;

/**
 * Created by x930073498 on 2019/6/19.
 */
public interface BaseItem extends HolderViewProvider {
    Object getData();

    void bind(CommonAdapter adapter, CommonHolder holder, int position, Object data, List<BaseItem> source, @NonNull List<Object> payloads);

}
