package com.x930073498.app;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.x930073498.adapter.BaseItem;
import com.x930073498.adapter.BaseItemWrapper;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    List<ParentData> data = createData();
                    parseData(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private int getChildCount() {
        return new Random().nextInt(7)+1;
    }

    private void parseData(List<ParentData> data) {
        List<BaseItem> items = new ArrayList<>();
        ParentData parentData;
        List<ChildData> childDataList;
        List<ChildData> temp;
        ChildData childData;
        for (int i = 0; i < data.size(); i++) {
            parentData = data.get(i);
            childDataList = parentData.getChildren();
            items.add(BaseItemWrapper.wrap(new TitleItem(), parentData.getTitle()));
            if (childDataList != null && childDataList.size() > 0) {
                temp = new ArrayList<>();
                for (int j = 0; j < childDataList.size(); j++) {
                    childData = childDataList.get(j);
                    childData.setIndex(items.size());
                    temp.add(childData);
                    if (j % childMaxItemCount == childMaxItemCount - 1) {
                        items.add(BaseItemWrapper.wrap(new ChildItem(), (Object) temp));
                        temp=new ArrayList<>();
                    }
                }
                if (!temp.isEmpty()) {
                    items.add(BaseItemWrapper.wrap(new ChildItem(), (Object) temp));
                }
            }
            items.add(new SpaceItem());
        }
        result.postValue(items);
    }


    private List<ParentData> createData() {
        List<ParentData> parentDataList = new ArrayList<>();
        List<ChildData> childDataList;
        ParentData parentData;
        ChildData childData;
        for (int i = 0; i < 50; i++) {
            parentData = new ParentData();
            parentData.setTitle("name " + i);
            int count = getChildCount();
            childDataList = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                childData = new ChildData();
                if (i == 0 && j == 0)
                    childData.setChecked(true);
                else childData.setChecked(false);
                childDataList.add(childData);
            }
            parentData.setChildren(childDataList);
            parentDataList.add(parentData);
        }
        return parentDataList;
    }

}
