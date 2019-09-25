package com.x930073498.recycler;

/**
 * 用于创建viewHolder
 */
public interface HolderFactory {
        ViewHolder create(FactoryParams params);
    }