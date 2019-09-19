package com.x930073498.app;

import com.x930073498.adapter.BaseItem;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by x930073498 on 2019/6/24.
 */
public class MainViewModel extends ViewModel {

    final MutableLiveData<List<BaseItem>> result = new MutableLiveData<>();


    private final int childMaxItemCount = 4;

    public void getData() {

    }

    private int getChildCount() {
        return new Random().nextInt(7)+1;
    }
}
