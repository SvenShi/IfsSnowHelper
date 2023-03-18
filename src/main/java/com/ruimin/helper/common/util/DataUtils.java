package com.ruimin.helper.common.util;

public class DataUtils {

    public static int mustPositive(int i, int defaultValue) {
        return i <= 0 ? defaultValue : i;
    }

}
