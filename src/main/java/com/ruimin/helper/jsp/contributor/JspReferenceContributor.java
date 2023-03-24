package com.ruimin.helper.jsp.contributor;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlElementType;
import com.ruimin.helper.jsp.provider.JspReferenceProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:25
 * @description
 */
public class JspReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlElementType.XML_ATTRIBUTE_VALUE),
            new JspReferenceProvider());
    }
}
