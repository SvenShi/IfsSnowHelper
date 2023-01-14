package com.ruimin.helper.contributor;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.impl.source.tree.JavaElementType;
import com.ruimin.helper.provider.reference.JavaToRqlxReferenceProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @author shiwei
 * @email shiwei@rmitec.cn
 * @date 2023/01/14 上午 03:25
 * @description
 */
public class JavaReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(JavaElementType.LITERAL_EXPRESSION),
            new JavaToRqlxReferenceProvider());
    }
}
