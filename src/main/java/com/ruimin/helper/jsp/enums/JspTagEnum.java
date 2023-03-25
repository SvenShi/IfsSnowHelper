package com.ruimin.helper.jsp.enums;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.ruimin.helper.common.util.StringUtils;
import com.ruimin.helper.jsp.constans.JspConstants;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    DATASET(JspConstants.DATASET_TAG_NAME, JspAttrEnum.PATH),
    /**
     * 按钮
     */
    BUTTON(JspConstants.BUTTON_TAG_NAME, JspAttrEnum.BUTTON_ID, JspAttrEnum.DATASET),
    /**
     * 表格
     */
    GRID(JspConstants.GRID_TAG_NAME, JspAttrEnum.DATASET, JspAttrEnum.PAGINATION_BAR, JspAttrEnum.FIELD_STR,
        JspAttrEnum.MORE_FIELD_STR),
    /**
     * 文件
     */
    FILE(JspConstants.FILE_TAG_NAME, JspAttrEnum.DATASET),
    /**
     * 查询
     */
    QUERY(JspConstants.QUERY_TAG_NAME, JspAttrEnum.DATASET, JspAttrEnum.FIELD_STR, JspAttrEnum.MORE_FIELD_STR),
    /**
     * treegrid
     */
    TREEGRID(JspConstants.TREEGRID_TAG_NAME, JspAttrEnum.DATASET, JspAttrEnum.FIELD_STR, JspAttrEnum.MORE_FIELD_STR),
    /**
     * 表单
     */
    FORM(JspConstants.FORM_TAG_NAME, JspAttrEnum.DATASET, JspAttrEnum.FIELD_STR, JspAttrEnum.MORE_FIELD_STR),
    /**
     * formgroup
     */
    FORMGROUP(JspConstants.FORMGROUP_TAG_NAME, JspAttrEnum.DATASET),
    /**
     * formfield
     */
    FORMFIELD(JspConstants.FORMFIELD_TAG_NAME, JspAttrEnum.DATASET), QUERYFIELD(JspConstants.QUERYFIELD_TAG_NAME,
        JspAttrEnum.DATASET),
    /**
     * querygroup
     */
    QUERYGROUP(JspConstants.QUERYGROUP_TAG_NAME, JspAttrEnum.DATASET),
    /**
     * 树
     */
    TREE(JspConstants.TREE_TAG_NAME, JspAttrEnum.DATASET),
    SFORM(JspConstants.SFORM_TAG_NAME, JspAttrEnum.FLOW_ID),
    /**
     * 到处
     */
    EXPORTER(JspConstants.EXPORTER_TAG_NAME, JspAttrEnum.DATASET, JspAttrEnum.FIELD_STR, JspAttrEnum.MORE_FIELD_STR);

    /**
     * 名字
     */
    private final String name;

    private final Set<JspAttrEnum> attrs;


    JspTagEnum(String name, JspAttrEnum... attrs) {
        this.name = name;
        this.attrs = Set.of(attrs);
    }

    public String getName() {
        return name;
    }

    public List<PsiReference> getReferences(XmlAttributeValue attributeValue) {
        String localName = XmlAttributeValuePattern.getLocalName(attributeValue);
        ArrayList<PsiReference> psiReferences = new ArrayList<>();
        for (JspAttrEnum attr : attrs) {
            if (attr.isTarget(localName)) {
                psiReferences.addAll(attr.getReferences(attributeValue));
            }
        }
        return psiReferences;
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

    public XmlTag findDatasetTagById(PsiElement element, String id) {
        XmlTag tagById = SnowJspUtils.findTagById(element, id, this);
        if (this.equals(DATASET)) {
            return tagById;
        }
        if (tagById != null) {
            XmlAttribute attribute = tagById.getAttribute(JspConstants.ATTR_NAME_DATASET);
            if (attribute != null) {
                String value = attribute.getValue();
                if (StringUtils.isNotBlank(value)) {
                    return SnowJspUtils.findTagById(element, value, DATASET);
                }
            }
        }
        return null;
    }
}
