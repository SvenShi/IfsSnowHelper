package com.ruimin.helper.common.enums;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.common.constants.SnowPageConstants;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.reference.SnowPageButtonDataSetReference;
import com.ruimin.helper.reference.SnowPageButtonIdReference;
import com.ruimin.helper.reference.SnowPageDataSetPathReference;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/03/11 下午 04:16
 * @description
 */
public enum PageTagEnum {
    /**
     * 数据集
     */
    DataSet(SnowPageConstants.SNOW_PAGE_DATASET_TAG_NAME) {
        @Override
        public PsiReference @NotNull [] getReference(XmlAttributeValue attributeValue) {
            String attributeName = XmlAttributeValuePattern.getLocalName(attributeValue);
            if (SnowPageConstants.DTST_ATTR_NAME_PATH.equals(attributeName)) {
                return new PsiReference[]{new SnowPageDataSetPathReference(attributeValue)};
            }
            return PsiReference.EMPTY_ARRAY;
        }
    },
    /**
     * 按钮
     */
    Button(SnowPageConstants.SNOW_PAGE_BUTTON_TAG_NAME) {
        @Override
        public PsiReference @NotNull [] getReference(XmlAttributeValue attributeValue) {
            String attributeName = XmlAttributeValuePattern.getLocalName(attributeValue);
            if (SnowPageConstants.BUTTON_ATTR_NAME_ID.equals(attributeName)) {
                return new PsiReference[]{new SnowPageButtonIdReference(attributeValue)};
            } else if (SnowPageConstants.BUTTON_ATTR_NAME_DATASET.equals(attributeName)) {
                return new PsiReference[]{new SnowPageButtonDataSetReference(attributeValue)};
            }
            return PsiReference.EMPTY_ARRAY;
        }
    };

    /**
     * 名字
     */
    private final String name;


    PageTagEnum(String name) {
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
