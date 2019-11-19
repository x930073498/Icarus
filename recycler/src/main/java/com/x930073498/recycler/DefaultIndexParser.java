package com.x930073498.recycler;

public class DefaultIndexParser implements IndexParser {
    @Override
    public int size(int sourceSize) {
        return sourceSize;
    }

    @Override
    public int getIndex(int size, int sourceSize, int position) {
        return position;
    }
}
