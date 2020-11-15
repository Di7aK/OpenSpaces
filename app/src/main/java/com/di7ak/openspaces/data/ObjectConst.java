package com.di7ak.openspaces.data;

import java.util.HashMap;
import java.util.Map;

public class ObjectConst {
    public static final Map<Integer, Integer> OBJECT_TYPE_TO_COMMENT_TYPE = new HashMap<Integer, Integer>() {
        {
            put(5, 42);
            put(6, 43);
            put(7, 44);
            put(25, 45);
            put(8, 51);
            put(9, 54);
            put(49, 54);
        }
    };
}
