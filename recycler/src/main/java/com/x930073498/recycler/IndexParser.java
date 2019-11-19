package com.x930073498.recycler;

public interface IndexParser {
    int size(int sourceSize);

    int getIndex(int size, int sourceSize, int position);

}
