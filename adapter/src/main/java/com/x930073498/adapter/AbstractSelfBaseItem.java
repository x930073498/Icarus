package com.x930073498.adapter;


/**
 * 自身提供创建 type并创建viewHolder
 *
 * @param <T>
 */
public abstract class AbstractSelfBaseItem<T> implements BaseItem<T>, TypeProvider<T>, HolderFactory {
    @Override
    public final HolderFactory createHolder(FactoryParams params) {
        return this;
    }


    @Override
    public final TypeProvider<T> getType(Bundle<T> bundle) {
        return this;
    }

}
