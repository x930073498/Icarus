package com.x930073498.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 包裹的数据源
 */
@SuppressWarnings("ALL")
public abstract class Bundle<T> {
    Bundle(BaseItem<T> item, T data) {
        this.item = item;
        this.data = data;
    }

    private T data;
    BaseItem<T> item;

    protected T getData() {
        return data;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bundle<?> bundle = (Bundle<?>) o;
        return equals(data, bundle.data);
    }
    private boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    @Override
    public int hashCode() {
        return hash(data);
    }
    private int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    public static <T> List<Bundle<T>> create(BaseItem<T> item, T... data) {
        return create(item, Arrays.asList(data));
    }

    public static <T> List<Bundle<T>> createTypeDelegate(BaseItem<T> item, TypeProvider<T> provider, T... data) {
        return create(new TypeBaseItem<>(provider, item), data);
    }
    public static <T> List<Bundle<T>> createFullDelegate(BaseItem<T> item, TypeProvider<T> provider, HolderFactory factory, T... data) {
        return create(new FullBaseItem<>(item,factory,provider), data);
    }


    public static <T, R extends TypeProvider<T> & HolderFactory> List<Bundle<T>> createDelegate(BaseItem<T> item, R delegate, T... data) {
        return create(new FullBaseItem<>(item, delegate, delegate), data);
    }

    public static <T> List<Bundle<T>> createFactoryDelegate(BaseItem<T> item, HolderFactory factory, T... data) {
        return create(new FactoryBaseItem<>(item, factory), data);
    }

    public static <T> List<Bundle<T>> create(BaseItem<T> item, List<T> data) {
        List<Bundle<T>> result = new ArrayList<>();
        if (data == null) return result;
        for (T temp : data
        ) {
            result.add(new SourceBundle<>(item, temp));
        }
        return result;
    }

    public static <T> List<Bundle<T>> createTypeDelegate(BaseItem<T> item, TypeProvider<T> provider, List<T> data) {
        BaseItem<T> temp = new TypeBaseItem<>(provider, item);
        return create(temp, data);
    }

    public static <T> List<Bundle<T>> createFactoryDelegate(BaseItem<T> item, HolderFactory factory, List<T> data) {
        BaseItem<T> temp = new FactoryBaseItem<>(item, factory);
        return create(temp, data);
    }
    public static <T> List<Bundle<T>> createFullDelegate(BaseItem<T> item, TypeProvider<T> provider, HolderFactory factory, List<T> data) {
        BaseItem<T> temp = new FullBaseItem<>(item, factory,provider);
        return create(temp, data);
    }

    public static <T, R extends TypeProvider<T> & HolderFactory> List<Bundle<T>> createDelegate(BaseItem<T> item, R delegate, List<T> data) {
        BaseItem<T> temp = new FullBaseItem<>(item, delegate, delegate);
        return create(temp, data);
    }
}
