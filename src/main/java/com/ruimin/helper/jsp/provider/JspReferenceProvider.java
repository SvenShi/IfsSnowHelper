package com.ruimin.helper.jsp.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.jsp.enums.JspTagEnum;
import com.ruimin.helper.jsp.utils.SnowJspUtils;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class JspReferenceProvider extends PsiReferenceProvider {

    @Override
    @NotNull
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        XmlAttributeValue attribute = (XmlAttributeValue) element;
        XmlTag tag = SnowJspUtils.findTag(attribute);
        if (tag != null) {
            List<PsiReference> references = JspTagEnum.getReferences(tag, attribute);
            if (CollectionUtils.isNotEmpty(references)) {
                return references.toArray(new PsiReference[0]);
            }

        }
        return PsiReference.EMPTY_ARRAY;
    }
}
