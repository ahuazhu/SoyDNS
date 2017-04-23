package com.ahuazhu.soy.utils;

import java.util.function.Function;

/**
 * Created by zhuzhengwen on 2017/4/23.
 */
public class Schedule {

    public static boolean retry(Function<?, Boolean> target, int times) {

        Boolean success = false;

        for (int i = 0; i < times && !success; i++) {
            success = target.apply(null);
        }

        return success;
    }
}
