package com.ruimin.helper.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 下午 10:57
 * @description
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * 索引所有
     *
     * @param str str
     * @param searchStr 搜索str
     * @return {@link List}<{@link Integer}>
     */
    public static List<Integer> indexOfAll(String str, String searchStr) {
        ArrayList<Integer> indexes = new ArrayList<>();
        if (org.apache.commons.lang.StringUtils.isEmpty(str) || org.apache.commons.lang.StringUtils.isEmpty(searchStr)) {
            return indexes;
        }

        int index = str.indexOf(searchStr);
        while (index != -1) {
            indexes.add(index);
            index = str.indexOf(searchStr, index + 1);
        }
        return indexes;
    }

    public static String removeQuot(String text) {
        return StringUtils.remove(text, "\"");
    }
}
