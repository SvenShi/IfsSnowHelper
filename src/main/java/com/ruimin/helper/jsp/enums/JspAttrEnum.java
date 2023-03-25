package com.ruimin.helper.jsp.enums;

import static com.ruimin.helper.jsp.constans.JspConstants.ATTR_NAME_DATASET;
import static com.ruimin.helper.jsp.constans.JspConstants.ATTR_NAME_FIELD_STR;
import static com.ruimin.helper.jsp.constans.JspConstants.ATTR_NAME_FLOW_ID;
import static com.ruimin.helper.jsp.constans.JspConstants.ATTR_NAME_ID;
import static com.ruimin.helper.jsp.constans.JspConstants.ATTR_NAME_MORE_FIELD_STR;
import static com.ruimin.helper.jsp.constans.JspConstants.ATTR_NAME_PAGINATION_BAR;
import static com.ruimin.helper.jsp.constans.JspConstants.ATTR_NAME_PATH;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.jsp.reference.JspButtonIdReference;
import com.ruimin.helper.jsp.reference.JspDataSetIdReference;
import com.ruimin.helper.jsp.reference.JspDataSetPathReference;
import com.ruimin.helper.jsp.reference.JspFlowIdReference;
import com.ruimin.helper.jsp.reference.JspPaginationbarReference;
import com.ruimin.helper.jsp.reference.JspTagFieldIdReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum JspAttrEnum {
    /**
     * 数据集路径
     */
    PATH(ATTR_NAME_PATH) {
        /**
         * 获取引用
         *
         * @param attributeValue 属性值
         * @return {@link List}<{@link PsiReference}>
         */
        @Override
        public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
            return Collections.singletonList(new JspDataSetPathReference(attributeValue));
        }
    },
    /**
     * 按钮id
     */
    BUTTON_ID(ATTR_NAME_ID) {
        /**
         * 获取引用
         *
         * @param attributeValue 属性值
         * @return {@link List}<{@link PsiReference}>
         */
        @Override
        public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
            return Collections.singletonList(new JspButtonIdReference(attributeValue, attributeValue.getValue()));
        }
    },
    /**
     * 数据集
     */
    DATASET(ATTR_NAME_DATASET) {
        /**
         * 获取引用
         *
         * @param attributeValue 属性值
         * @return {@link List}<{@link PsiReference}>
         */
        @Override
        public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
            return Collections.singletonList(new JspDataSetIdReference(attributeValue));
        }
    },
    /**
     * 分页栏
     */
    PAGINATION_BAR(ATTR_NAME_PAGINATION_BAR) {
        /**
         * 获取引用
         *
         * @param attributeValue 属性值
         * @return {@link List}<{@link PsiReference}>
         */
        @Override
        public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
            String paginationbar = attributeValue.getValue();
            if (paginationbar.contains("{") || paginationbar.contains("<") || paginationbar.contains("}")
                || paginationbar.contains(">") || paginationbar.contains("%") || paginationbar.contains("$")) {
                return Collections.emptyList();
            }
            ArrayList<PsiReference> psiReferences = new ArrayList<>();
            String[] split = paginationbar.split(",");
            int prevIndex = 0;
            for (String buttonId : split) {
                int startIndex = paginationbar.indexOf(buttonId, prevIndex);
                if (startIndex >= 0) {
                    prevIndex = startIndex + buttonId.length();
                    psiReferences.add(
                        new JspPaginationbarReference(attributeValue, buttonId, startIndex + 1, prevIndex + 1));
                }
            }
            return psiReferences;
        }
    },
    /**
     * 属性
     */
    FIELD_STR(ATTR_NAME_FIELD_STR) {
        /**
         * 获取引用
         *
         * @param attributeValue 属性值
         * @return {@link List}<{@link PsiReference}>
         */
        @Override
        public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
            String fieldStr = attributeValue.getValue();
            if (fieldStr.contains("{") || fieldStr.contains("<") || fieldStr.contains("}") || fieldStr.contains(">")
                || fieldStr.contains("%") || fieldStr.contains("$")) {
                return Collections.emptyList();
            }
            String realFieldStr = fieldStr.replaceAll("\\[\\d+]", "");
            String[] split = realFieldStr.split(",");
            ArrayList<PsiReference> psiReferences = new ArrayList<>();
            int prevIndex = 0;
            for (String fieldId : split) {
                int startIndex = fieldStr.indexOf(fieldId, prevIndex);
                if (startIndex >= 0) {
                    prevIndex = startIndex + fieldId.length();
                    psiReferences.add(
                        new JspTagFieldIdReference(attributeValue, fieldId, startIndex + 1, prevIndex + 1));
                }
            }
            return psiReferences;
        }
    },
    /**
     * 属性
     */
    MORE_FIELD_STR(ATTR_NAME_MORE_FIELD_STR) {
        /**
         * 获取引用
         *
         * @param attributeValue 属性值
         * @return {@link List}<{@link PsiReference}>
         */
        @Override
        public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
            return FIELD_STR.getReferences(attributeValue);
        }
    },
    /**
     * flow Id
     */
    FLOW_ID(ATTR_NAME_FLOW_ID) {
        /**
         * 获取引用
         *
         * @param attributeValue 属性值
         * @return {@link List}<{@link PsiReference}>
         */
        @Override
        public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
            String flowId = attributeValue.getValue();
            if (flowId.contains("?")) {
                flowId = StringUtils.substringBeforeLast(flowId, "?");
            }
            if (flowId.contains("{") || flowId.contains("<") || flowId.contains("}") || flowId.contains(">")
                || flowId.contains("%") || flowId.contains("$")) {
                return Collections.emptyList();
            }
            return Collections.singletonList(new JspFlowIdReference(attributeValue, flowId));
        }
    };

    private final String name;

    JspAttrEnum(String name) {
        this.name = name;
    }

    public boolean isTarget(XmlAttributeValue value) {
        return value != null && name.equals(XmlAttributeValuePattern.getLocalName(value));
    }

    public boolean isTarget(String name) {
        return this.name.equals(name);
    }

    public boolean isTarget(XmlAttribute attribute) {
        return isTarget(attribute.getName());
    }

    /**
     * 获取引用
     *
     * @param attributeValue 属性值
     * @return {@link List}<{@link PsiReference}>
     */
    public abstract List<PsiReference> getReferences(XmlAttributeValue attributeValue);
}
