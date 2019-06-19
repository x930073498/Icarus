package com.x930073498.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x930073498 on 2019/6/19.
 */
public class TestData {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<TestData> create() {
        List<TestData> data = new ArrayList<>();
        TestData temp;
        for (int i = 0; i < 20; i++) {
            temp = new TestData();
            temp.setName("name " + i);
            data.add(temp);
        }
        return data;
    }
}
