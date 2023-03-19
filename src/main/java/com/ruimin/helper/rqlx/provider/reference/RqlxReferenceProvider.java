package com.ruimin.helper.rqlx.provider.reference;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.rqlx.constans.RqlxConstants;
import com.ruimin.helper.rqlx.dom.model.Mapper;
import com.ruimin.helper.rqlx.reference.RqlxIdJavaReference;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class RqlxReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        XmlFile xmlFile = (XmlFile) element.getContainingFile();
        XmlTag rootTag = xmlFile.getRootTag();
        if (rootTag != null && Mapper.TAG_NAME.equals(rootTag.getName())) {
            XmlAttributeValue attribute = (XmlAttributeValue) element;
            String localName = XmlAttributeValuePattern.getLocalName(attribute);
            if (RqlxConstants.ATTR_NAME_ID.equals(localName)) {
                return new PsiReference[]{new RqlxIdJavaReference(attribute)};
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
