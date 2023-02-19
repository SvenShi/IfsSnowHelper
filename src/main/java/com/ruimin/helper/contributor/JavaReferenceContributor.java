package com.ruimin.helper.contributor;

import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
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
        registrar.registerReferenceProvider(PsiJavaPatterns.psiLiteral(),
            new JavaToRqlxReferenceProvider());

    }
}
