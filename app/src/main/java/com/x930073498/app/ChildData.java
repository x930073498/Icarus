package com.x930073498.app;

/**
 * Created by x930073498 on 2019/6/24.
 */
public class ChildData {


    private int index = -1;
    private boolean isChecked = false;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "ChildData{" +
                "index=" + index +
                ", isChecked=" + isChecked +
                '}';
    }
}
