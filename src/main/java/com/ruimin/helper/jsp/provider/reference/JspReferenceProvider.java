package com.ruimin.helper.jsp.provider.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.jsp.enums.JspTagEnum;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class JspReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        XmlAttributeValue attribute = (XmlAttributeValue) element;
        XmlTag tag = SnowJspUtils.findTag(attribute);
        if (tag != null) {
            if (JspTagEnum.DataSet.isTarget(tag)) {
                return JspTagEnum.DataSet.getReference(attribute);
            } else if (JspTagEnum.Button.isTarget(tag)) {
                return JspTagEnum.Button.getReference(attribute);
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
