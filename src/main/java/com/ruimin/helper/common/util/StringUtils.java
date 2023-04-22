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

    public static final String[] EMPTY_ARRAY = new String[0];

    /**
     * 索引所有
     *
     * @param str str
     * @param searchStr 搜索str
     * @return {@link List}<{@link Integer}>
     */
    public static List<Integer> indexOfAll(String str, String searchStr) {
        ArrayList<Integer> indexes = new ArrayList<>();
        if (org.apache.commons.lang.StringUtils.isEmpty(str) || org.apache.commons.lang.StringUtils.isEmpty(
            searchStr)) {
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
        return StringUtils.remove(text, "\"","'");
    }

    public static String remove(String str, String... removes) {
        for (String remove : removes) {
            str = remove(str, remove);
        }
        return str;
    }

    /**
     * 以最后出现的一个字符作为分割点分割字符串
     *
     * @param str str
     * @param lastStr 最后力量
     * @return {@link String[]}
     */
    public static String[] splitLast(String str, String lastStr) {
        if (isNotEmpty(str) && isNotEmpty(lastStr)) {
            int pos = str.lastIndexOf(lastStr);
            if (pos >= 0) {
                String[] strings = new String[2];
                strings[0] = str.substring(0, pos);
                strings[1] = str.substring(pos + 1);
                return strings;
            }
        }
        return EMPTY_ARRAY;
    }
}
