package com.ruimin.helper.dtst.provider.reference;

import com.google.common.collect.Sets;
import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.dtst.constans.DataSetConstants;
import com.ruimin.helper.dtst.dom.model.Data;
import com.ruimin.helper.dtst.reference.DatasetDataSourceReference;
import com.ruimin.helper.dtst.reference.DatasetFlowIdReference;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class DataSetReferenceProvider extends PsiReferenceProvider {


    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        XmlFile xmlFile = (XmlFile) element.getContainingFile();
        XmlTag rootTag = xmlFile.getRootTag();
        if (StringUtils.isNotBlank(element.getText()) && rootTag != null && Data.TAG_NAME.equals(rootTag.getName())) {
            XmlAttributeValue attribute = (XmlAttributeValue) element;
            String localName = XmlAttributeValuePattern.getLocalName(attribute);
            if (DataSetConstants.XML_TAG_FLOWID_ATTRIBUTE_NAME.equals(localName)) {
                return new PsiReference[]{new DatasetFlowIdReference(attribute)};
            } else if (DataSetConstants.XML_TAG_DATASOURCE_ATTRIBUTE_NAME.equals(localName)) {
                String[] split = ((XmlAttributeValue) element).getValue().split(":");
                if (split.length >= 2 && !DataSetConstants.NOT_IN_DATASOURCE_TAG.contains(split[0])) {
                    return new PsiReference[]{new DatasetDataSourceReference(attribute, element.getText().indexOf(":"), split[1])};
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
