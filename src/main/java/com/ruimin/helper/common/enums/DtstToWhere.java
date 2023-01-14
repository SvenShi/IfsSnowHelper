package com.ruimin.helper.common.enums;

import com.google.common.collect.Sets;
import com.ruimin.helper.dom.dtst.model.Data;
import com.ruimin.helper.dom.dtst.model.Field;
import java.util.Set;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2022/11/13 下午 04:00
 * @description 文件类型
 */
public enum DtstToWhere {
    JSP(Sets.newHashSet(Data.class.getSimpleName())), DTST(Sets.newHashSet(Field.class.getSimpleName()));

    /**
     * xml标签名
     */
    private final Set<String> XmlTagNames;

    DtstToWhere(Set<String> xmlTagNames) {
        XmlTagNames = xmlTagNames;
    }

    public Set<String> getXmlTagNames() {
        return XmlTagNames;
    }


    /**
     * 根据xml标签名获取导航到哪里的枚举类
     *
     * @param xmlTagName xml标记名称
     * @return {@link DtstToWhere} 返回null 则为没找到
     */
    public static DtstToWhere getToWhere(String xmlTagName) {
        for (DtstToWhere value : values()) {
            if (value.getXmlTagNames().contains(xmlTagName)) {
                return value;
            }
        }
        return null;
    }
}
