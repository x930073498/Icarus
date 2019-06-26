package com.x930073498.app;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.x930073498.adapter.BaseItem;
import com.x930073498.adapter.CommonAdapter;
import com.x930073498.adapter.CommonHolder;
import com.x930073498.adapter.LayoutBaseItem;
import com.x930073498.boat.BoatManager;

import java.util.List;

/**
 * Created by x930073498 on 2019/6/24.
 */
public class TitleItem extends LayoutBaseItem {
    @Override
    public int getLayout(Object data, int position) {
        return R.layout.layout_item_title;
    }

    @Override
    public void bind(CommonAdapter adapter, CommonHolder holder, int position, Object data, List<BaseItem> source, @NonNull List<Object> payloads) {
        TextView tvTitle = holder.getView(R.id.tvTitle);
        tvTitle.setText(data.toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), SecondActivity.class));
            }
        });
    }
}
