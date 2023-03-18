package com.ruimin.helper.jsp.enums;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.jsp.reference.JspButtonDataSetReference;
import com.ruimin.helper.jsp.reference.JspButtonIdReference;
import com.ruimin.helper.jsp.reference.JspDataSetPathReference;
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
    DataSet(JspConstants.DATASET_TAG_NAME) {
        @Override
        public PsiReference @NotNull [] getReference(XmlAttributeValue attributeValue) {
            String attributeName = XmlAttributeValuePattern.getLocalName(attributeValue);
            if (JspConstants.ATTR_NAME_PATH.equals(attributeName)) {
                return new PsiReference[]{new JspDataSetPathReference(attributeValue)};
            }
            return PsiReference.EMPTY_ARRAY;
        }
    },
    /**
     * 按钮
     */
    Button(JspConstants.BUTTON_TAG_NAME) {
        @Override
        public PsiReference @NotNull [] getReference(XmlAttributeValue attributeValue) {
            String attributeName = XmlAttributeValuePattern.getLocalName(attributeValue);
            if (JspConstants.ATTR_NAME_ID.equals(attributeName)) {
                return new PsiReference[]{new JspButtonIdReference(attributeValue)};
            } else if (JspConstants.ATTR_NAME_DATASET.equals(attributeName)) {
                return new PsiReference[]{new JspButtonDataSetReference(attributeValue)};
            }
            return PsiReference.EMPTY_ARRAY;
        }
    };

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

    public abstract PsiReference @NotNull [] getReference(XmlAttributeValue attributeValue);

    public boolean isTarget(@NotNull XmlTag xmlTag) {
        String tagName = xmlTag.getName();
        return StringUtils.isNotBlank(tagName) && name.equals(tagName);
    }
}
