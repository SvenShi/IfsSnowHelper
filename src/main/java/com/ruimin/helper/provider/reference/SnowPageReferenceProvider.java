package com.ruimin.helper.provider.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.ruimin.helper.common.enums.PageTagEnum;
import com.ruimin.helper.common.util.SnowPageUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:54
 * @description
 */
public class SnowPageReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
        @NotNull ProcessingContext context) {
        XmlAttributeValue attribute = (XmlAttributeValue) element;
        XmlTag tag = SnowPageUtils.findTag(attribute);
        if (tag != null) {
            if (PageTagEnum.DataSet.isTarget(tag)) {
                return PageTagEnum.DataSet.getReference(attribute);
            } else if (PageTagEnum.Button.isTarget(tag)) {
                return PageTagEnum.Button.getReference(attribute);
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
