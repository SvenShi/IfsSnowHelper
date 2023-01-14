package com.ruimin.helper.provider.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.constants.CommonConstants;
import com.ruimin.helper.constants.DtstConstants;
import com.ruimin.helper.dom.dtst.model.Command;
import com.ruimin.helper.dom.dtst.model.Define;
import com.ruimin.helper.dom.dtst.model.FlowIdDomElement;
import com.ruimin.helper.reference.DatasetReference;
import com.ruimin.helper.util.JavaUtils;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class DatasetReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        XmlAttributeValue attribute = (XmlAttributeValue) element;
        PsiElement parent = attribute.getParent();
        if (parent instanceof XmlAttribute){
            XmlAttribute xmlAttribute = (XmlAttribute) parent;
            if (DtstConstants.XML_TAG_FLOWID_ATTRIBUTE_NAME.equals(xmlAttribute.getName())) {
                String flowId = attribute.getValue();
                if (StringUtils.isNotBlank(flowId)) {
                    return new PsiReference[]{new DatasetReference(attribute)};
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
