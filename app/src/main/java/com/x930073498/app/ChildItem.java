package com.x930073498.app;

import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.x930073498.adapter.BaseItem;
import com.x930073498.adapter.CommonAdapter;
import com.x930073498.adapter.CommonHolder;
import com.x930073498.adapter.LayoutBaseItem;

import java.util.List;

/**
 * Created by x930073498 on 2019/6/24.
 */
public class ChildItem extends LayoutBaseItem {
    @Override
    public int getLayout(Object data, int position) {
        List<ChildData> list = (List<ChildData>) data;
        int size = list.size();
        switch (size) {
            case 1:
                return R.layout.layout_item_child_1;
            case 2:
                return R.layout.layout_item_child_2;
            case 3:
                return R.layout.layout_item_child_3;
            default:
                return R.layout.layout_item_child_4;
        }
    }

    @Override
    public void bind(CommonAdapter adapter, CommonHolder holder, int position, Object data, final List<BaseItem> source, @NonNull List<Object> payloads) {
        List<ChildData> list = (List<ChildData>) data;
        RadioButton checkBox1, checkBox2, checkBox3, checkBox4;

        if (list.size() == 1) {
            checkBox1 = holder.getView(R.id.checkbox1);
            clearListener(checkBox1);
            checkBox1.setChecked(list.get(0).isChecked());
            setCheckListener(checkBox1, list.get(0), adapter, source);
        } else if (list.size() == 2) {
            checkBox1 = holder.getView(R.id.checkbox1);
            clearListener(checkBox1);
            checkBox1.setChecked(list.get(0).isChecked());
            setCheckListener(checkBox1, list.get(0), adapter, source);
            checkBox2 = holder.getView(R.id.checkbox2);
            clearListener(checkBox2);
            checkBox2.setChecked(list.get(1).isChecked());
            setCheckListener(checkBox2, list.get(1), adapter, source);
        } else if (list.size() == 3) {
            checkBox1 = holder.getView(R.id.checkbox1);
            clearListener(checkBox1);
            checkBox1.setChecked(list.get(0).isChecked());
            setCheckListener(checkBox1, list.get(0), adapter, source);
            checkBox2 = holder.getView(R.id.checkbox2);
            clearListener(checkBox2);
            checkBox2.setChecked(list.get(1).isChecked());
            setCheckListener(checkBox2, list.get(1), adapter, source);
            checkBox3 = holder.getView(R.id.checkbox3);
            clearListener(checkBox3);
            checkBox3.setChecked(list.get(2).isChecked());
            setCheckListener(checkBox3, list.get(2), adapter, source);
        } else if (list.size() == 4) {
            checkBox1 = holder.getView(R.id.checkbox1);
            clearListener(checkBox1);
            checkBox1.setChecked(list.get(0).isChecked());
            setCheckListener(checkBox1, list.get(0), adapter, source);
            checkBox2 = holder.getView(R.id.checkbox2);
            clearListener(checkBox2);
            checkBox2.setChecked(list.get(1).isChecked());
            setCheckListener(checkBox2, list.get(1), adapter, source);
            checkBox3 = holder.getView(R.id.checkbox3);
            clearListener(checkBox3);
            checkBox3.setChecked(list.get(2).isChecked());
            setCheckListener(checkBox3, list.get(2), adapter, source);
            checkBox4 = holder.getView(R.id.checkbox4);
            clearListener(checkBox4);
            checkBox4.setChecked(list.get(3).isChecked());
            setCheckListener(checkBox4, list.get(3), adapter, source);
        }


    }


    private void clearListener(RadioButton... buttons) {
        for (RadioButton button : buttons
        ) {
            if (button == null) continue;
            button.setOnCheckedChangeListener(null);
        }
    }

    private void setCheckListener(final RadioButton checkBox, final ChildData data, final CommonAdapter adapter, final List<BaseItem> source) {
        if (checkBox == null) return;
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setChecked(isChecked);
                if (isChecked) {
                    int index = -1;
                    for (BaseItem item : source
                    ) {
                        index++;
                        if (item.getData() instanceof List) {
                            List<ChildData> childData = (List<ChildData>) item.getData();
                            for (ChildData temp : childData
                            ) {
                                if (temp == data) continue;
                                if (temp.isChecked()) {
                                    temp.setChecked(false);
                                    adapter.notifyItemChanged(index);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
