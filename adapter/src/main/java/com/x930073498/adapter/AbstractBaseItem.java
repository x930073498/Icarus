package com.x930073498.adapter;

public abstract class AbstractBaseItem<T> implements BaseItem<T>, TypeProvider<T>, HolderFactory {
    @Override
    public final HolderFactory createHolder(FactoryParams params) {
        return this;
    }


    @Override
    public final TypeProvider<T> getType(Bundle<T> bundle) {
        return this;
    }

}
