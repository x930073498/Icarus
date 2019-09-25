package com.x930073498.context;

public enum State {
    CREATED(1),
    STARTED(2),
    RESUMED(3),
    PAUSED(4),
    STOPPED(5),
    SAVE_INSTANCE_STATE(6),
    DESTROYED(-1);
    int value;

    State(int value) {
        this.value = value;
    }
}