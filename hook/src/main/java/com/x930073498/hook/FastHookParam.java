package com.x930073498.hook;

import java.util.Arrays;

public class FastHookParam {
    public Object receiver;
    public Object[] args;
    public Object result;
    public boolean replace;

    @Override
    public String toString() {
        return "FastHookParam{" +
                "receiver=" + receiver +
                ", args=" + Arrays.toString(args) +
                ", result=" + result +
                ", replace=" + replace +
                '}';
    }
}
