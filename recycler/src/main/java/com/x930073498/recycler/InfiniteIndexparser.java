package com.x930073498.recycler;

public class InfiniteIndexparser implements IndexParser {
    @Override
    public int size(int sourceSize) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getIndex(int size, int sourceSize, int position) {
        return position % sourceSize;
    }
}
