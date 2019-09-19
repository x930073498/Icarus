package com.x930073498.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SourceItem<T> {
    SourceItem(BaseItem<T> item, T data) {
        this.item = item;
        this.data = data;
    }

    private T data;
    BaseItem<T> item;

    protected T getData() {
        return data;
    }

    @SafeVarargs
    public static <T> List<SourceItem<T>> create(BaseItem<T> item, T... data) {
        return create(item, Arrays.asList(data));
    }

    public static <T> List<SourceItem<T>> create(BaseItem<T> item, TypeProvider<T> provider, T... data) {
        return create(new TypeBaseItem<>(provider, item), data);
    }

    public static <T> List<SourceItem<T>> create(BaseItem<T> item, List<T> data) {
        List<SourceItem<T>> result = new ArrayList<>();
        if (data == null) return result;
        for (T temp : data
        ) {
            result.add(new Bundle<>(item, temp));
        }
        return result;
    }

    public static <T>List<SourceItem<T>> create(BaseItem<T> item,TypeProvider<T> provider,List<T> data){
        BaseItem<T> temp=new TypeBaseItem<>(provider, item);
        return create(temp,data);
    }
}
