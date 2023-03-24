package com.ruimin.helper.js.contributor;

import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.ruimin.helper.js.provider.JSExpressionReferenceProvider;
import com.ruimin.helper.js.provider.JSLiteralReferenceProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:25
 * @description
 */
public class JavaScriptReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(JSElementTypes.REFERENCE_EXPRESSION),
            new JSExpressionReferenceProvider());
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(JSElementTypes.LITERAL_EXPRESSION),
            new JSLiteralReferenceProvider());
    }
}
