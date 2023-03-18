package com.ruimin.helper.jsp.enums;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.jsp.reference.JspDataSetIdReference;
import com.ruimin.helper.jsp.reference.JspButtonIdReference;
import com.ruimin.helper.jsp.reference.JspDataSetPathReference;
import com.ruimin.helper.jsp.reference.JspGridPaginationbarReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/11 下午 04:16
 * @description
 */
public enum JspTagEnum {
    /**
     * 数据集
     */
    DATASET(JspConstants.DATASET_TAG_NAME) {
        @Override
        public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
            String attributeName = XmlAttributeValuePattern.getLocalName(attributeValue);
            if (JspConstants.ATTR_NAME_PATH.equals(attributeName)) {
                return Collections.singletonList(new JspDataSetPathReference(attributeValue));
            }
            return Collections.emptyList();
        }
    },
    /**
     * 按钮
     */
    BUTTON(JspConstants.BUTTON_TAG_NAME) {
        @Override
        public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
            String attributeName = XmlAttributeValuePattern.getLocalName(attributeValue);
            if (JspConstants.ATTR_NAME_ID.equals(attributeName)) {
                return Collections.singletonList(new JspButtonIdReference(attributeValue));
            } else if (JspConstants.ATTR_NAME_DATASET.equals(attributeName)) {
                return Collections.singletonList(new JspDataSetIdReference(attributeValue));
            }
            return Collections.emptyList();
        }
    }, GRID(JspConstants.GRID_TAG_NAME) {
        @Override
        public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
            String attributeName = XmlAttributeValuePattern.getLocalName(attributeValue);
            if (JspConstants.ATTR_NAME_PAGINATION_BAR.equals(attributeName)) {
                String paginationbar = attributeValue.getValue();
                if (StringUtils.isNotBlank(paginationbar)) {
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
                                new JspGridPaginationbarReference(attributeValue, buttonId, startIndex + 1,
                                    prevIndex + 1));
                        }
                    }
                    return psiReferences;
                }
            } else if (JspConstants.ATTR_NAME_DATASET.equals(attributeName)) {
                return Collections.singletonList(new JspDataSetIdReference(attributeValue));
            }
            return Collections.emptyList();
        }
    }, FILE(JspConstants.FILE_TAG_NAME), QUERY(JspConstants.QUERY_TAG_NAME), TREEGRID(
        JspConstants.TREEGRID_TAG_NAME), FORM(JspConstants.FORM_TAG_NAME), FORMGROUP(
        JspConstants.FORMGROUP_TAG_NAME), FORMFIELD(JspConstants.FORMFIELD_TAG_NAME), QUERYFIELD(
        JspConstants.QUERYFIELD_TAG_NAME), QUERYGROUP(JspConstants.QUERYGROUP_TAG_NAME), TREE(
        JspConstants.TREE_TAG_NAME), EXPORTER(JspConstants.EXPORTER_TAG_NAME);

    /**
     * 名字
     */
    private final String name;


    JspTagEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
        String attributeName = XmlAttributeValuePattern.getLocalName(attributeValue);
        if (JspConstants.ATTR_NAME_DATASET.equals(attributeName)) {
            return Collections.singletonList(new JspDataSetIdReference(attributeValue));
        }
        return Collections.emptyList();
    }

    ;

    public boolean isTarget(@NotNull XmlTag xmlTag) {
        String tagName = xmlTag.getName();
        return StringUtils.isNotBlank(tagName) && name.equals(tagName);
    }

    public static List<PsiReference> getReferences(XmlTag tag, XmlAttributeValue attributeValue) {
        ArrayList<PsiReference> psiReferences = new ArrayList<>();
        for (JspTagEnum value : values()) {
            if (value.isTarget(tag)) {
                List<PsiReference> references = value.getReferences(attributeValue);
                psiReferences.addAll(references);
            }
        }
        return psiReferences;
    }
}
