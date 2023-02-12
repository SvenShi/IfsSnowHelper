package com.ruimin.helper.provider.reference;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.common.constants.DtstConstants;
import com.ruimin.helper.dom.dtst.model.Data;
import com.ruimin.helper.reference.DatasetToJavaReference;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class DataSetToJavaReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        XmlFile xmlFile = (XmlFile) element.getContainingFile();
        XmlTag rootTag = xmlFile.getRootTag();
        if (rootTag != null && Data.TAG_NAME.equals(rootTag.getName())) {
            XmlAttributeValue attribute = (XmlAttributeValue) element;
            String localName = XmlAttributeValuePattern.getLocalName(attribute);
            if (DtstConstants.XML_TAG_FLOWID_ATTRIBUTE_NAME.equals(localName)) {
                return new PsiReference[]{new DatasetToJavaReference(attribute)};
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
