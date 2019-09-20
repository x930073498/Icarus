package com.x930073498.adapter;

/**
 * 用于viewType解析
 * @param <T>
 */
public interface TypeProvider<T> {
    int type(InitialBundle<T> bundle);
}
