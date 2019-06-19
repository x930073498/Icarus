package com.x930073498.adapter;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class CommonHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> map=new SparseArray<>();

    public CommonHolder(@NonNull View itemView) {
        super(itemView);
    }

    public <T extends View> T getView(int id){
        View view=map.get(id);
        if (view==null){
            view=itemView.findViewById(id);
            map.put(id,view);
        }
        return (T) view;
    }

}
